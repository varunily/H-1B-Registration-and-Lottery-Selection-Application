package com.h1b.lottery.repository;

import com.h1b.lottery.domain.model.Registration;
import com.h1b.lottery.domain.model.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByEmployerIdOrderByCreatedAtDesc(Long employerId);

    List<Registration> findByFiscalYearAndStatus(Integer fiscalYear, RegistrationStatus status);

    List<Registration> findByFiscalYear(Integer fiscalYear);

    long countByFiscalYearAndStatus(Integer fiscalYear, RegistrationStatus status);

    Optional<Registration> findByEmployerIdAndBeneficiaryIdAndFiscalYear(Long employerId, Long beneficiaryId, Integer fiscalYear);

    List<Registration> findByFiscalYearOrderByCreatedAtDesc(Integer fiscalYear);
}
