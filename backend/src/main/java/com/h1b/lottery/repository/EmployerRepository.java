package com.h1b.lottery.repository;

import com.h1b.lottery.domain.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByFein(String fein);
}
