package com.example.backend_engine.repository;

import com.example.backend_engine.model.FinancialMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FinancialMetricsRepository extends JpaRepository<FinancialMetrics, Long> {
    Optional<FinancialMetrics> findTopByTickerOrderByLastUpdatedDesc(String ticker);
}
