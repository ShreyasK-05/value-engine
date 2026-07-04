package com.example.backend_engine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "financial_metrics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ticker;

    private double priceToEarnings;
    private double returnOnEquity;
    private double debtToEquity;
    private double intrinsicValue;
    private double currentPrice;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = LocalDateTime.now();
    }
}
