package co.com.nequi.model.gateways;

import co.com.nequi.model.franchise.Franchise;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FranchiseRepositoryGateway {

    Mono<Franchise> save(Franchise franchise);
    Mono<Franchise> findById(UUID id);
    Mono<Franchise> findByName(String name);
    Mono<Franchise> update(Franchise franchise);
}
