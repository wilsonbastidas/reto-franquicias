package co.com.nequi.model.product;

import co.com.nequi.model.branch.Branch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Product {
    private UUID id;
    private String name;
    private BigDecimal stock;
    private Branch branch;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
