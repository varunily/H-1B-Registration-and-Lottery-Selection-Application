package com.h1b.lottery.web.dto;

import com.h1b.lottery.domain.model.enums.EducationLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BeneficiaryCreateRequest(
        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Country of citizenship is required")
        String countryOfCitizenship,

        @NotNull(message = "Highest education is required")
        EducationLevel highestEducation
) {
}
