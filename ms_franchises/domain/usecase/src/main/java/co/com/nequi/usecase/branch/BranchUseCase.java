package co.com.nequi.usecase.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class BranchUseCase {
    private final BranchRepositoryGateway branchRepository;
    private final FranchiseRepositoryGateway franchiseRepository;

    public Mono<Branch> createBranch(Branch branch) {
        return franchiseRepository.findById(branch.getFranchise().getId())
                .switchIfEmpty(Mono.error( new NotFoundException("Franchise not found")))
                .flatMap(franchise ->
                    branchRepository.findByName(branch.getName())
                        .hasElement()
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(
                                        new ConflictException("Branch with name " + branch.getName()+ " already exists"));
                            }
                            branch.setFranchise(franchise);
                            return branchRepository.save(branch);
                        })
                );
    }

    public Mono<Branch> updateBranchName(UUID id, String name) {
        return branchRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Branch not found")))
                .flatMap(existing -> branchRepository.findByName(name)
                        .flatMap(found -> {
                            if (!found.getId().equals(id)) {
                                return Mono.error(new ConflictException("Branch with name " + name + " already exists"));
                            }
                            return Mono.just(existing);
                        })
                        .defaultIfEmpty(existing)
                        .flatMap(branch -> {
                            branch.setName(name);
                            branch.setUpdatedAt(LocalDateTime.now());
                            return branchRepository.update(branch);
                        })
                );
    }
}
