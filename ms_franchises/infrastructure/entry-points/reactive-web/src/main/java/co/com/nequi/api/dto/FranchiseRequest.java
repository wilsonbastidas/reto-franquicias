package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotBlank;

public record FranchiseRequest(
        @NotBlank(message = "The name is required")
        String name
) { }
