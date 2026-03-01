package com.h1b.lottery.web.dto;

import com.h1b.lottery.domain.model.enums.RegistrationStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RegistrationResponse(
        Long id,
        Long employerId,
        Long beneficiaryId,
        String beneficiaryName,
        Integer fiscalYear,
        BigDecimal offeredSalary,
        String workLocation,
        boolean mastersCapEligible,
        RegistrationStatus status,
        OffsetDateTime submittedAt,
        OffsetDateTime selectedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
