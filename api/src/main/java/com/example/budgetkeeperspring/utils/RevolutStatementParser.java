package com.example.budgetkeeperspring.utils;

import com.example.budgetkeeperspring.entity.AccountTransaction;
import com.opencsv.CSVReader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Parses a Revolut account statement exported as .xlsx or .csv, one file per currency
 * (columns: Type, Product, Started Date, Completed Date, Description, Amount, Fee, Currency, State, Balance).
 * ponytail: reads the xlsx zip with stdlib instead of Apache POI; Revolut's export is a single flat sheet.
 */
public final class RevolutStatementParser {

    public static final Set<String> COMPLETED_STATES = Set.of("ZAKOŃCZONO", "COMPLETED");

    private static final int COLUMNS = 10;
    private static final Set<String> FIRST_HEADERS = Set.of("Rodzaj", "Type");
    private static final LocalDateTime EXCEL_EPOCH = LocalDateTime.of(1899, 12, 30, 0, 0);
    private static final DateTimeFormatter TEXT_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern XML_ESCAPE = Pattern.compile("_x([0-9A-Fa-f]{4})_");
    private static final Pattern C1_CONTROL = Pattern.compile("[\\u0080-\\u009F]");
    // "ą" is C4 85; Revolut drops the 0x85 (NEL) byte, leaving a lone "Ä"
    private static final Pattern LOST_A_OGONEK = Pattern.compile("\u00C4(?![\\u0080-\\u00BF])");

    private RevolutStatementParser() {
    }

    public static List<AccountTransaction> parse(InputStream in, Long accountId) throws IOException {
        byte[] bytes = in.readAllBytes();
        boolean zip = bytes.length > 1 && bytes[0] == 'P' && bytes[1] == 'K';
        List<String[]> rows = zip ? xlsxRows(bytes) : csvRows(bytes);
        if (rows.isEmpty() || !FIRST_HEADERS.contains(rows.get(0)[0])) {
            throw new IllegalArgumentException("Nieznany format wyciągu - oczekiwano nagłówka 'Rodzaj'/'Type'");
        }

        List<AccountTransaction> result = new ArrayList<>();
        for (String[] r : rows.subList(1, rows.size())) {
            if (r[2] == null || r[5] == null) {
                continue;
            }
            result.add(AccountTransaction.builder()
                    .accountId(accountId)
                    .type(r[0])
                    .product(r[1])
                    .description(r[4] == null ? "" : r[4])
                    .startedAt(date(r[2]))
                    .completedAt(r[3] == null ? null : date(r[3]))
                    .amount(new BigDecimal(r[5]).setScale(2, RoundingMode.HALF_UP))
                    .fee(r[6] == null ? BigDecimal.ZERO : new BigDecimal(r[6]).setScale(2, RoundingMode.HALF_UP))
                    .currency(r[7])
                    .state(r[8])
                    .balance(r[9] == null ? null : new BigDecimal(r[9]).setScale(2, RoundingMode.HALF_UP))
                    .build());
        }
        return result;
    }

    private static List<String[]> csvRows(byte[] bytes) throws IOException {
        String text = new String(bytes, StandardCharsets.UTF_8).replace("\uFEFF", "");
        List<String[]> result = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new StringReader(text))) {
            for (String[] line : reader) {
                String[] row = new String[COLUMNS];
                for (int i = 0; i < Math.min(line.length, COLUMNS); i++) {
                    row[i] = line[i].isEmpty() ? null : line[i];
                }
                result.add(row);
            }
        } catch (RuntimeException e) { // opencsv iterator wraps parse errors
            throw new IllegalArgumentException("Nie udało się odczytać pliku csv", e);
        }
        return result;
    }

    private static List<String[]> xlsxRows(byte[] bytes) throws IOException {
        Map<String, byte[]> entries = unzip(bytes);
        byte[] sheet = entries.get("xl/worksheets/sheet1.xml");
        if (sheet == null) {
            throw new IllegalArgumentException("To nie jest plik xlsx z wyciągiem Revoluta");
        }
        List<String> shared = entries.containsKey("xl/sharedStrings.xml")
                ? sharedStrings(entries.get("xl/sharedStrings.xml")) : List.of();
        return rows(sheet, shared);
    }

    private static Map<String, byte[]> unzip(byte[] bytes) throws IOException {
        Map<String, byte[]> entries = new HashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            for (ZipEntry e; (e = zip.getNextEntry()) != null; ) {
                if (e.getName().equals("xl/sharedStrings.xml") || e.getName().equals("xl/worksheets/sheet1.xml")) {
                    entries.put(e.getName(), zip.readNBytes(20_000_000)); // ponytail: caps zip-bomb expansion
                }
            }
        }
        return entries;
    }

    private static List<String> sharedStrings(byte[] xml) {
        List<String> result = new ArrayList<>();
        NodeList items = parseXml(xml).getElementsByTagName("si");
        for (int i = 0; i < items.getLength(); i++) {
            result.add(text((Element) items.item(i)));
        }
        return result;
    }

    private static List<String[]> rows(byte[] xml, List<String> shared) {
        List<String[]> result = new ArrayList<>();
        NodeList rows = parseXml(xml).getElementsByTagName("row");
        for (int i = 0; i < rows.getLength(); i++) {
            String[] row = new String[COLUMNS];
            NodeList cells = ((Element) rows.item(i)).getElementsByTagName("c");
            for (int j = 0; j < cells.getLength(); j++) {
                Element c = (Element) cells.item(j);
                int col = c.getAttribute("r").charAt(0) - 'A';
                if (col < 0 || col >= COLUMNS) {
                    continue;
                }
                NodeList v = c.getElementsByTagName("v");
                String value = switch (c.getAttribute("t")) {
                    case "s" -> v.getLength() == 0 ? null : shared.get(Integer.parseInt(v.item(0).getTextContent()));
                    case "inlineStr" -> text(c);
                    default -> v.getLength() == 0 ? null : v.item(0).getTextContent();
                };
                row[col] = value == null || value.isEmpty() ? null : value;
            }
            result.add(row);
        }
        return result;
    }

    private static String text(Element parent) {
        StringBuilder sb = new StringBuilder();
        NodeList ts = parent.getElementsByTagName("t");
        for (int i = 0; i < ts.getLength(); i++) {
            sb.append(ts.item(i).getTextContent());
        }
        return fixEncoding(sb.toString());
    }

    /**
     * Revolut writes UTF-8 bytes as Latin-1 characters ("PÅ\u0082atnoÅ\u009bÄ\u0087" instead of "Płatność"),
     * with control characters escaped as _xHHHH_. Undo both; leave correctly encoded text untouched.
     */
    static String fixEncoding(String s) {
        s = XML_ESCAPE.matcher(s).replaceAll(m -> String.valueOf((char) Integer.parseInt(m.group(1), 16)));
        if (s.chars().anyMatch(ch -> ch > 0xFF)) {
            return s;
        }
        String decoded = strictUtf8(s);
        if (decoded != null || !C1_CONTROL.matcher(s).find()) {
            return decoded != null ? decoded : s;
        }
        String repaired = LOST_A_OGONEK.matcher(s).replaceAll("\u00C4\u0085");
        decoded = strictUtf8(repaired);
        return decoded != null ? decoded : new String(repaired.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    private static String strictUtf8(String latin1) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .decode(ByteBuffer.wrap(latin1.getBytes(StandardCharsets.ISO_8859_1))).toString();
        } catch (CharacterCodingException e) {
            return null;
        }
    }

    private static LocalDateTime date(String value) {
        if (value.contains("-")) {
            return LocalDateTime.parse(value, TEXT_DATE);
        }
        return EXCEL_EPOCH.plusSeconds(Math.round(Double.parseDouble(value) * 86_400));
    }

    private static org.w3c.dom.Document parseXml(byte[] xml) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            return f.newDocumentBuilder().parse(new ByteArrayInputStream(xml));
        } catch (Exception e) {
            throw new IllegalArgumentException("Nie udało się odczytać pliku xlsx", e);
        }
    }
}
