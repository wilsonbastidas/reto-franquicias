package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateBranchNameRequest(
        @NotNull(message = "Branch ID cannot be null")
        UUID id,
        @NotBlank(message = "Branch name cannot be blank")
        String name
) {
}
