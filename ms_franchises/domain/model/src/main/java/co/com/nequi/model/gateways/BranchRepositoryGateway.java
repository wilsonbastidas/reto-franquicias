package co.com.nequi.model.gateways;

import co.com.nequi.model.branch.Branch;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BranchRepositoryGateway {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(UUID id);
}
