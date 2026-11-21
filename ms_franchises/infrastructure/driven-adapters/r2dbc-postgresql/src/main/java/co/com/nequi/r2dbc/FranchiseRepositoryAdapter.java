package co.com.nequi.r2dbc;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.exceptions.PersistenceException;
import co.com.nequi.model.franchise.gateways.FranchiseRepositoryGateway;
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
        LocalDateTime now = LocalDateTime.now();
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID());
        }
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setUpdatedAt(now);
        entity.setNew(true);

        return franchiseRepository.save(entity)
                .map(mapper::toDomain)
                .onErrorMap(throwable -> new PersistenceException("Error saving franchise", throwable));
    }
}
