package com.h1b.lottery.web.dto;

import com.h1b.lottery.domain.model.enums.EducationLevel;

import java.time.OffsetDateTime;

public record BeneficiaryResponse(
        Long id,
        Long employerId,
        String firstName,
        String lastName,
        String email,
        String countryOfCitizenship,
        EducationLevel highestEducation,
        boolean mastersCapEligible,
        OffsetDateTime createdAt
) {
}
