package com.h1b.lottery.web.dto;

import com.h1b.lottery.domain.model.enums.RegistrationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegistrationCreateRequest(
        @NotNull(message = "Beneficiary id is required")
        Long beneficiaryId,

        @NotNull(message = "Fiscal year is required")
        @Min(value = 2025, message = "Fiscal year must be 2025 or later")
        Integer fiscalYear,

        @NotNull(message = "Offered salary is required")
        @DecimalMin(value = "1.0", inclusive = true, message = "Offered salary must be positive")
        BigDecimal offeredSalary,

        @NotBlank(message = "Work location is required")
        String workLocation,

        RegistrationStatus status
) {
}
