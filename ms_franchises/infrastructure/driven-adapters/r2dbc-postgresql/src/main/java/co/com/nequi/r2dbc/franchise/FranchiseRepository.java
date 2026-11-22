package co.com.nequi.r2dbc.franchise;

import co.com.nequi.r2dbc.entities.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, UUID> {

    Mono<FranchiseEntity> findByName(String name);

}
