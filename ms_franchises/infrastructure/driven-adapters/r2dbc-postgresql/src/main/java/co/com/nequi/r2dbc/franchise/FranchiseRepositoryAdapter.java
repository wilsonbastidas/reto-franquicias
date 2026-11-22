package co.com.nequi.r2dbc.franchise;

import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.exceptions.ConflictException;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import co.com.nequi.r2dbc.entities.FranchiseEntity;
import co.com.nequi.r2dbc.mapper.FranchiseEntityMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class FranchiseRepositoryAdapter implements FranchiseRepositoryGateway {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper mapper;

    public FranchiseRepositoryAdapter(FranchiseRepository franchiseRepository, FranchiseEntityMapper mapper) {
        this.franchiseRepository = franchiseRepository;
        this.mapper = mapper;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = mapper.toEntity(franchise);
        entity.setCreatedAt(LocalDateTime.now());
        return franchiseRepository.findByName(entity.getName())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ConflictException("Franchise with name " + entity.getName()+ " already exists"));
                    }
                    return franchiseRepository.save(entity)
                            .map(mapper::toDomain)
                            .onErrorMap(error -> new PersistenceException("Error saving franchise", error));
                });

    }

    @Override
    public Mono<Franchise> findById(UUID id) {
        return franchiseRepository.findById(id)
                .map(mapper::toDomain)
                .onErrorMap(throwable -> new PersistenceException("Error finding franchise", throwable));
    }
}
