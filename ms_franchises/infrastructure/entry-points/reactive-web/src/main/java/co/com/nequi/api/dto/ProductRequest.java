package co.com.nequi.api.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotNull(message = "Product name cannot be null")
        String name,
        @NotNull(message = "Product stock cannot be null")
        BigDecimal stock,
        @NotNull(message = "Branch ID cannot be null")
        String branchId
) { }
