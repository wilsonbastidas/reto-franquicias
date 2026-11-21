package co.com.nequi.model.franchise.gateways;

import co.com.nequi.model.franchise.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseRepositoryGateway {

    Mono<Franchise> save(Franchise franchise);
}
