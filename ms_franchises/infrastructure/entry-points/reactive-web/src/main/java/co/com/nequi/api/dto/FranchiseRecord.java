package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotBlank;

public record FranchiseRecord(
        @NotBlank(message = "The name is required")
        String name
) { }
