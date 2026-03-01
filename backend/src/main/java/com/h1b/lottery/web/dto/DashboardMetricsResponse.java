package com.h1b.lottery.web.dto;

public record DashboardMetricsResponse(
        Integer fiscalYear,
        long employers,
        long beneficiaries,
        long draftRegistrations,
        long submittedRegistrations,
        long selectedRegistrations,
        long notSelectedRegistrations,
        long lotteryRuns
) {
}
