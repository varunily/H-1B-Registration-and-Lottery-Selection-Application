package com.h1b.lottery.web.dto;

import java.time.OffsetDateTime;

public record EmployerResponse(
        Long id,
        String legalName,
        String fein,
        String contactEmail,
        OffsetDateTime createdAt
) {
}
