package co.com.nequi.r2dbc.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.r2dbc.entities.BranchEntity;
import co.com.nequi.r2dbc.mapper.BranchEntityMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class BranchRepositoryAdapter implements BranchRepositoryGateway {
    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;

    public BranchRepositoryAdapter(BranchRepository branchRepository, BranchEntityMapper branchEntityMapper) {
        this.branchRepository = branchRepository;
        this.branchEntityMapper = branchEntityMapper;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = branchEntityMapper.toEntity(branch);
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);

        return branchRepository.save(entity)
                .map(saved -> {
                    Branch savedBranch = branchEntityMapper.toDomain(saved);
                    savedBranch.setFranchise(branch.getFranchise());
                    return savedBranch;
                })
                .onErrorMap(throwable -> new PersistenceException("Error saving branch", throwable));
    }

    @Override
    public Mono<Branch> findById(UUID id) {
        return branchRepository.findById(id)
                .map(branchEntityMapper::toDomain)
                .onErrorMap(throwable -> new PersistenceException("Error finding branch", throwable));
    }

    @Override
    public Mono<Branch> findByName(String name) {
        return branchRepository.findByName(name)
                .map(branchEntityMapper::toDomain)
                .onErrorMap(throwable -> new PersistenceException("Error finding branch", throwable));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        BranchEntity entity = branchEntityMapper.toEntity(branch);
        return branchRepository.save(entity)
                .map(saved -> {
                    Branch updatedBranch = branchEntityMapper.toDomain(saved);
                    updatedBranch.setFranchise(branch.getFranchise());
                    return updatedBranch;
                })
                .onErrorMap(throwable -> new PersistenceException("Error updating branch", throwable));
    }
}
