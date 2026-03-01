package com.h1b.lottery.web.dto;

import com.h1b.lottery.domain.model.enums.LotteryRunStatus;

import java.time.OffsetDateTime;

public record LotteryRunResponse(
        Long id,
        Integer fiscalYear,
        Integer regularCap,
        Integer mastersCap,
        Long seed,
        Integer totalSubmitted,
        Integer selectedRegular,
        Integer selectedMasters,
        LotteryRunStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime completedAt
) {
}
