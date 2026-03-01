package com.h1b.lottery.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LotteryRunRequest(
        @NotNull(message = "Fiscal year is required")
        @Min(value = 2025, message = "Fiscal year must be 2025 or later")
        Integer fiscalYear,

        @NotNull(message = "Regular cap is required")
        @Min(value = 1, message = "Regular cap must be at least 1")
        Integer regularCap,

        @NotNull(message = "Masters cap is required")
        @Min(value = 0, message = "Masters cap must be zero or more")
        Integer mastersCap,

        Long seed
) {
}
