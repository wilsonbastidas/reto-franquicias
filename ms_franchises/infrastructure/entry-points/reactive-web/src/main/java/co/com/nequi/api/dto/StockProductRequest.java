package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record StockProductRequest(
        @NotNull(message = "Product ID cannot be null")
        UUID id,
        @NotNull(message = "Stock cannot be null")
        BigDecimal stock
) {
}
