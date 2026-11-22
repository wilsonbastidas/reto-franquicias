package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateProductNameRequest(
        @NotNull(message = "Product ID cannot be null")
        UUID id,
        @NotBlank(message = "Product name cannot be blank")
        String name
) {
}
