package com.example.budgetkeeperspring.service;

import com.example.budgetkeeperspring.dto.PortfolioSnapshotDTO;
import com.example.budgetkeeperspring.entity.PortfolioSnapshot;
import com.example.budgetkeeperspring.repository.PortfolioSnapshotRepository;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PortfolioSnapshotService {

    private final PortfolioSnapshotRepository portfolioSnapshotRepository;
    private final FireStageService fireStageService;

    @Value("${myfund.api.url}")
    private String apiUrl;

    @Value("${myfund.api.key}")
    private String apiKey;

    private HttpClient httpClient;

    @PostConstruct
    void init() {
        httpClient = HttpClient.newHttpClient();
    }

    public List<PortfolioSnapshotDTO> findAll() {
        return portfolioSnapshotRepository.findAllByOrderByDateAsc()
                .stream()
                .map(e -> PortfolioSnapshotDTO.builder()
                        .id(e.getId())
                        .date(e.getDate())
                        .value(e.getValue())
                        .investedCapital(e.getInvestedCapital())
                        .build())
                .toList();
    }

    public PortfolioSnapshotDTO save(LocalDate date, BigDecimal value) {
        return save(date, value, null);
    }

    @Transactional
    public PortfolioSnapshotDTO save(LocalDate date, BigDecimal value, BigDecimal investedCapital) {
        if (portfolioSnapshotRepository.findByDate(date).isPresent()) {
            return null;
        }

        PortfolioSnapshot snapshot = PortfolioSnapshot.builder().date(date).build();
        snapshot.setValue(value);
        if (investedCapital != null) {
            snapshot.setInvestedCapital(investedCapital);
        }

        PortfolioSnapshot saved = portfolioSnapshotRepository.save(snapshot);
        fireStageService.checkAndMarkCrossedStages(date, value);
        return PortfolioSnapshotDTO.builder()
                .id(saved.getId())
                .date(saved.getDate())
                .value(saved.getValue())
                .investedCapital(saved.getInvestedCapital())
                .build();
    }

    public void fetchFromMyFundAndSave() {
        String url = String.format(apiUrl, apiKey);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("myFund API returned status {}", response.statusCode());
                return;
            }
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            LocalDate today = LocalDate.now();

            JsonObject portfel = root.getAsJsonObject("portfel");
            if (portfel == null || portfel.get("wartosc") == null) {
                log.error("Unexpected myFund API response — missing portfel.wartosc. Body: {}", response.body());
                return;
            }
            BigDecimal value = portfel.get("wartosc").getAsBigDecimal();

            BigDecimal investedCapital = null;
            JsonObject wkladWCzasie = root.getAsJsonObject("wkladWCzasie");
            if (wkladWCzasie != null && wkladWCzasie.has(today.toString())) {
                investedCapital = wkladWCzasie.get(today.toString()).getAsBigDecimal();
            }

            save(today, value, investedCapital);
            log.info("Fetched portfolio value={} investedCapital={} for {}", value, investedCapital, today);
        } catch (IOException | InterruptedException e) {
            log.error("Failed to fetch portfolio snapshot from myFund", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Unexpected error while parsing myFund API response", e);
        }
    }
}
