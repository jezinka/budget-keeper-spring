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

    public List<PortfolioSnapshotDTO> findAll() {
        return portfolioSnapshotRepository.findAllByOrderByDateAsc()
                .stream()
                .map(e -> PortfolioSnapshotDTO.builder()
                        .id(e.getId())
                        .date(e.getDate())
                        .value(e.getValue())
                        .build())
                .toList();
    }

    public PortfolioSnapshotDTO save(LocalDate date, BigDecimal value) {
        if (portfolioSnapshotRepository.findByDate(date).isPresent()) {
            log.info("Portfolio snapshot for {} already exists, skipping", date);
            return null;
        }
        PortfolioSnapshot saved = portfolioSnapshotRepository.save(
                PortfolioSnapshot.builder()
                        .date(date)
                        .value(value)
                        .build()
        );
        fireStageService.checkAndMarkCrossedStages(date, value);
        return PortfolioSnapshotDTO.builder()
                .id(saved.getId())
                .date(saved.getDate())
                .value(saved.getValue())
                .build();
    }

    public void fetchFromMyFundAndSave() {
        String url = String.format(apiUrl, apiKey);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("myFund API returned status {}", response.statusCode());
                return;
            }
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
            BigDecimal value = root.getAsJsonObject("portfel").get("wartosc").getAsBigDecimal();
            save(LocalDate.now(), value);
            log.info("Fetched portfolio value {} for {}", value, LocalDate.now());
        } catch (IOException | InterruptedException e) {
            log.error("Failed to fetch portfolio snapshot from myFund", e);
            Thread.currentThread().interrupt();
        }
    }
}
