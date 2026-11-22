package co.com.nequi.r2dbc.branch;

import co.com.nequi.r2dbc.entities.BranchEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BranchRepository extends ReactiveCrudRepository<BranchEntity, UUID> {

    Mono<BranchEntity> findByName(String name);
}
