package com.example.backend_engine.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "company_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfile {
    @Id
    private String ticker; // e.g., "TCS.NS", "RELIANCE.NS"

    @Column(nullable = false)
    private String companyName;

    private String industry;
    private String sector;
}
