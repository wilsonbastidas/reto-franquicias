package co.com.nequi.r2dbc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Table("products")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductEntity {

    @Id
    private UUID id;

    private String name;

    private BigDecimal stock;

    @Column(name = "branch_id")
    private UUID branchId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
