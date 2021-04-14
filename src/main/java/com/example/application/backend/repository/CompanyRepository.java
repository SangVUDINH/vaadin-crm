package com.example.application.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.backend.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}