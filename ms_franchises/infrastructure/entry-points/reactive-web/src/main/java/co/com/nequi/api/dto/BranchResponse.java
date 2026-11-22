package co.com.nequi.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BranchResponse(
        UUID id,
        String name,
        FranchiseResponse franchise,
        LocalDateTime createdAt
) {
}
