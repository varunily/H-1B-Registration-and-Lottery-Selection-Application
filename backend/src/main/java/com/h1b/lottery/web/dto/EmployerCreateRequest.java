package com.h1b.lottery.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmployerCreateRequest(
        @NotBlank(message = "Legal name is required")
        String legalName,

        @NotBlank(message = "FEIN is required")
        @Pattern(regexp = "^[0-9]{9}$", message = "FEIN must be exactly 9 digits")
        String fein,

        @NotBlank(message = "Contact email is required")
        @Email(message = "Contact email must be valid")
        String contactEmail
) {
}
