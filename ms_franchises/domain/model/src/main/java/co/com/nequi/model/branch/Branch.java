package co.com.nequi.model.branch;

import co.com.nequi.model.franchise.Franchise;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Branch {
    private UUID id;
    private String name;
    private Franchise franchise;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
