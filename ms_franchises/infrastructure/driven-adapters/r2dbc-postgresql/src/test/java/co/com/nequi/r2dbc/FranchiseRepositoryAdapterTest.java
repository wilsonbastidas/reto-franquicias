package co.com.nequi.r2dbc;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.exceptions.PersistenceException;
import co.com.nequi.r2dbc.entities.FranchiseEntity;
import co.com.nequi.r2dbc.mapper.FranchiseEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseRepositoryAdapterTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private FranchiseEntityMapper mapper;

    @InjectMocks
    private FranchiseRepositoryAdapter adapter;

    @Test
    void shouldSaveFranchiseSuccessfully() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Franchise franchise = Franchise.builder().name("Franquicia").build();
        FranchiseEntity entity = new FranchiseEntity();
        entity.setName("Franquicia");
        FranchiseEntity savedEntity = new FranchiseEntity(id, "Franquicia", now, now, false);
        Franchise savedDomain = Franchise.builder().id(id).name("Franquicia").createdAt(now).updatedAt(now).build();

        when(mapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        StepVerifier.create(adapter.save(franchise))
                .expectNext(savedDomain)
                .verifyComplete();
    }

    @Test
    void shouldMapErrorToPersistenceException() {
        Franchise franchise = Franchise.builder().name("Franquicia").build();
        FranchiseEntity entity = new FranchiseEntity();
        entity.setName("Franquicia");

        when(mapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.save(any(FranchiseEntity.class))).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.save(franchise))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldKeepExistingIdAndCreatedAt() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Franchise franchise = Franchise.builder().id(id).name("Franquicia").createdAt(createdAt).build();
        FranchiseEntity entity = new FranchiseEntity(id, "Franquicia", createdAt, null, false);
        FranchiseEntity savedEntity = new FranchiseEntity(id, "Franquicia", createdAt, LocalDateTime.now(), false);
        Franchise savedDomain = Franchise.builder().id(id).name("Franquicia").createdAt(createdAt).updatedAt(savedEntity.getUpdatedAt()).build();

        when(mapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        StepVerifier.create(adapter.save(franchise))
                .expectNext(savedDomain)
                .verifyComplete();
    }
}