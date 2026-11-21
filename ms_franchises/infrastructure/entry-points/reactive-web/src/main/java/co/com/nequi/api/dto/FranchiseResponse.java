package co.com.nequi.api.dto;

import java.time.LocalDateTime;

public record FranchiseResponse(
        String id,
        String name,
        LocalDateTime createdAt
) {
}
