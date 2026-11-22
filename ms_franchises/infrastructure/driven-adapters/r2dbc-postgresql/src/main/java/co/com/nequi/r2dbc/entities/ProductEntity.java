package co.com.nequi.r2dbc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
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

    @Column("branch_id")
    private UUID branchId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
