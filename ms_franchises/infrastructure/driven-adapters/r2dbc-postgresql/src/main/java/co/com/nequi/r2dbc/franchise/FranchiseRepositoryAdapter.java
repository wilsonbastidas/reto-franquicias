package co.com.nequi.r2dbc.franchise;

import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import co.com.nequi.r2dbc.entities.FranchiseEntity;
import co.com.nequi.r2dbc.mapper.FranchiseEntityMapper;
import co.com.nequi.resiliencehelper.ReactiveResilienceHelper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class FranchiseRepositoryAdapter implements FranchiseRepositoryGateway {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper mapper;
    private final ReactiveResilienceHelper resilienceHelper;

    public FranchiseRepositoryAdapter(FranchiseRepository franchiseRepository,
                                      FranchiseEntityMapper mapper,
                                      ReactiveResilienceHelper resilienceHelper) {
        this.franchiseRepository = franchiseRepository;
        this.mapper = mapper;
        this.resilienceHelper = resilienceHelper;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = mapper.toEntity(franchise);
        entity.setCreatedAt(LocalDateTime.now());
        return resilienceHelper.protectMono(
                franchiseRepository.save(entity)
                        .map(mapper::toDomain),
                "franchise-save"
        ).onErrorMap(error -> new PersistenceException("Error saving franchise", error));
    }

    @Override
    public Mono<Franchise> findById(UUID id) {
        return resilienceHelper.protectMono(
                franchiseRepository.findById(id)
                        .map(mapper::toDomain),
                "franchise-findById"
        ).onErrorMap(throwable -> new PersistenceException("Error finding franchise", throwable));
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        return resilienceHelper.protectMono(
                franchiseRepository.findByName(name)
                        .map(mapper::toDomain),
                "franchise-findByName"
        ).onErrorMap(throwable -> new PersistenceException("Error finding franchise", throwable));
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        FranchiseEntity entity = mapper.toEntity(franchise);
        return resilienceHelper.protectMono(
                franchiseRepository.save(entity)
                        .map(mapper::toDomain),
                "franchise-update"
        ).onErrorMap(error -> new PersistenceException("Error updating franchise", error));
    }
}