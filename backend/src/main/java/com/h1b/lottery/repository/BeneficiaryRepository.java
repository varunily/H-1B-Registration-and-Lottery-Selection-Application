package com.h1b.lottery.repository;

import com.h1b.lottery.domain.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByEmployerIdOrderByCreatedAtDesc(Long employerId);
}
