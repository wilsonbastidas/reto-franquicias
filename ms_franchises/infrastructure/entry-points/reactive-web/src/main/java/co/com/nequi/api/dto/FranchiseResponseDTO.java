package co.com.nequi.api.dto;

import java.time.LocalDateTime;

public record FranchiseResponseDTO(
        String id,
        String name,
        LocalDateTime createdAt
) {
}
