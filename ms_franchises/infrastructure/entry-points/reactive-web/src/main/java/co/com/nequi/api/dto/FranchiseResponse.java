package co.com.nequi.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FranchiseResponse(
        String id,
        String name,
        LocalDateTime createdAt
) {
}
