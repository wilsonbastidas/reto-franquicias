package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateFranchiseNameRequest(
        @NotNull(message = "Franchise ID cannot be null")
        UUID id,
        @NotBlank(message = "Franchise name cannot be blank")
        String name
) {
}
