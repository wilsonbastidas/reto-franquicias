package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BranchRequest(
        @NotBlank(message = "The name is required")
        String name,
        @NotNull(message = "The franchise ID is required")
        UUID franchiseId) {
}
