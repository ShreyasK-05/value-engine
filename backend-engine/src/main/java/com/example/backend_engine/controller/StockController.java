package com.example.backend_engine.controller;

import com.example.backend_engine.model.FinancialMetrics;
import com.example.backend_engine.repository.FinancialMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final FinancialMetricsRepository repository;

    @Cacheable(value = "stocks", key = "#ticker")
    @GetMapping("/{ticker}")
    public ResponseEntity<FinancialMetrics> getStockMetrics(@PathVariable String ticker) {
        return repository.findTopByTickerOrderByLastUpdatedDesc(ticker)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}