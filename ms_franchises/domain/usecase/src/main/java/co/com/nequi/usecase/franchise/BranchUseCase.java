package co.com.nequi.usecase.franchise;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {
    private final BranchRepositoryGateway branchRepository;
    private final FranchiseRepositoryGateway franchiseRepository;

    public Mono<Branch> createBranch(Branch branch) {
        return franchiseRepository.findById(branch.getFranchise().getId())
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMap(franchise -> {
                    branch.setFranchise(franchise);
                    return branchRepository.save(branch);
                });
    }
}
