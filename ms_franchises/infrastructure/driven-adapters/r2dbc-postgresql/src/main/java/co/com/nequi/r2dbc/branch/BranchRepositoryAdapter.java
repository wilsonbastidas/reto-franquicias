package co.com.nequi.r2dbc.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.r2dbc.entities.BranchEntity;
import co.com.nequi.r2dbc.mapper.BranchEntityMapper;
import co.com.nequi.resiliencehelper.ReactiveResilienceHelper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class BranchRepositoryAdapter implements BranchRepositoryGateway {
    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;
    private final ReactiveResilienceHelper resilienceHelper;

    public BranchRepositoryAdapter(BranchRepository branchRepository,
                                   BranchEntityMapper branchEntityMapper,
                                   ReactiveResilienceHelper resilienceHelper) {
        this.branchRepository = branchRepository;
        this.branchEntityMapper = branchEntityMapper;
        this.resilienceHelper = resilienceHelper;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = branchEntityMapper.toEntity(branch);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return resilienceHelper.protectMono(
                branchRepository.save(entity)
                        .map(saved -> {
                            Branch savedBranch = branchEntityMapper.toDomain(saved);
                            savedBranch.setFranchise(branch.getFranchise());
                            return savedBranch;
                        }),
                "branch-save"
        ).onErrorMap(throwable -> new PersistenceException("Error saving branch", throwable));
    }

    @Override
    public Mono<Branch> findById(UUID id) {
        return resilienceHelper.protectMono(
                branchRepository.findById(id)
                        .map(branchEntityMapper::toDomain),
                "branch-findById"
        ).onErrorMap(throwable -> new PersistenceException("Error finding branch", throwable));
    }

    @Override
    public Mono<Branch> findByName(String name) {
        return resilienceHelper.protectMono(
                branchRepository.findByName(name)
                        .map(branchEntityMapper::toDomain),
                "branch-findByName"
        ).onErrorMap(throwable -> new PersistenceException("Error finding branch", throwable));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        BranchEntity entity = branchEntityMapper.toEntity(branch);
        return resilienceHelper.protectMono(
                branchRepository.save(entity)
                        .map(saved -> {
                            Branch updatedBranch = branchEntityMapper.toDomain(saved);
                            updatedBranch.setFranchise(branch.getFranchise());
                            return updatedBranch;
                        }),
                "branch-update"
        ).onErrorMap(throwable -> new PersistenceException("Error updating branch", throwable));
    }
}