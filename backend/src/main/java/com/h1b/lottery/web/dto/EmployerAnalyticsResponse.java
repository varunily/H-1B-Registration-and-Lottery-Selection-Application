package com.h1b.lottery.web.dto;

public record EmployerAnalyticsResponse(
        Long employerId,
        String employerName,
        Integer fiscalYear,
        long totalRegistrations,
        long selected,
        double selectionRate
) {
}
