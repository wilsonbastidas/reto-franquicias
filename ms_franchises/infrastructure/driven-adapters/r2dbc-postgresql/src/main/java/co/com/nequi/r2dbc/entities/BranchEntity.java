package co.com.nequi.r2dbc.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("branches")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BranchEntity {

    @Id
    private UUID id;

    private String name;

    @Column("franchise_id")
    private UUID franchiseId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
