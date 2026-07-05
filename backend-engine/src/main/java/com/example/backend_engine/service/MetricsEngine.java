package com.example.backend_engine.service;

import com.example.backend_engine.model.*;
import com.example.backend_engine.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MetricsEngine {

    private final FinancialMetricsRepository metricsRepository;
    private final CompanyProfileRepository profileRepository;
    private final ObjectMapper objectMapper;
    private final CacheManager cacheManager; // Inject CacheManager

    @Transactional
    @KafkaListener(topics = "raw-stock-data", groupId = "value-engine-group-v3")
    public void consumeMarketData(String message) {
        try {
            StockPayload payload = objectMapper.readValue(message, StockPayload.class);

            // 1. Manually evict cache for this ticker
            if (cacheManager.getCache("stocks") != null) {
                cacheManager.getCache("stocks").evict(payload.getTicker());
            }

            // 2. Business Logic
            profileRepository.findById(payload.getTicker()).orElseGet(() -> {
                CompanyProfile newProfile = new CompanyProfile(payload.getTicker(), payload.getTicker() + " Corp", "Unassigned", "Unassigned");
                return profileRepository.save(newProfile);
            });

            double eps = payload.getTrailingPE() > 0 ? payload.getCurrentPrice() / payload.getTrailingPE() : 0.0;
            double growthRate = payload.getReturnOnEquity() * 100;
            double intrinsicValue = eps > 0 ? eps * (8.5 + 2 * growthRate) : payload.getCurrentPrice();

            FinancialMetrics metrics = new FinancialMetrics();
            metrics.setTicker(payload.getTicker());
            metrics.setCurrentPrice(payload.getCurrentPrice());
            metrics.setPriceToEarnings(payload.getTrailingPE());
            metrics.setReturnOnEquity(payload.getReturnOnEquity());
            metrics.setDebtToEquity(payload.getDebtToEquity());
            metrics.setIntrinsicValue(intrinsicValue);
            metrics.setLastUpdated(LocalDateTime.now());

            metricsRepository.save(metrics);
            System.out.println("Processed & Saved Valuation for: " + payload.getTicker());

        } catch (Exception e) {
            System.err.println("Stream Processing Error: " + e.getMessage());
        }
    }
}