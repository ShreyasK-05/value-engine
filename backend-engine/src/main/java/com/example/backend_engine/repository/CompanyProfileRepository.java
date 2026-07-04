package com.example.backend_engine.repository;

import com.example.backend_engine.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, String> {
}
