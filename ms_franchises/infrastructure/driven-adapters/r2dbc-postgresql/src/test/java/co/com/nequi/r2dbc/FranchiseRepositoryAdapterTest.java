package co.com.nequi.r2dbc;

import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.r2dbc.entities.FranchiseEntity;
import co.com.nequi.r2dbc.franchise.FranchiseRepository;
import co.com.nequi.r2dbc.franchise.FranchiseRepositoryAdapter;
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
import static org.mockito.ArgumentMatchers.anyString;
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
        FranchiseEntity savedEntity = new FranchiseEntity(id, "Franquicia", now, now);
        Franchise savedDomain = Franchise.builder().id(id).name("Franquicia").createdAt(now).updatedAt(now).build();

        when(mapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(franchiseRepository.save(any(FranchiseEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.toDomain(savedEntity)).thenReturn(savedDomain);

        StepVerifier.create(adapter.save(franchise))
                .expectNext(savedDomain)
                .verifyComplete();
    }

    @Test
    void shouldThrowConflictWhenFranchiseAlreadyExists() {
        Franchise franchise = Franchise.builder().name("Franquicia").build();
        FranchiseEntity entity = new FranchiseEntity();
        entity.setName("Franquicia");
        FranchiseEntity existing = new FranchiseEntity(UUID.randomUUID(), "Franquicia", LocalDateTime.now(), LocalDateTime.now());

        when(mapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.findByName("Franquicia")).thenReturn(Mono.just(existing));

        StepVerifier.create(adapter.save(franchise))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldMapErrorToPersistenceException() {
        Franchise franchise = Franchise.builder().name("Franquicia").build();
        FranchiseEntity entity = new FranchiseEntity();
        entity.setName("Franquicia");

        when(mapper.toEntity(franchise)).thenReturn(entity);
        when(franchiseRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(franchiseRepository.save(any(FranchiseEntity.class))).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.save(franchise))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        FranchiseEntity entity = new FranchiseEntity(id, "Franquicia", now, now);
        Franchise domain = Franchise.builder().id(id).name("Franquicia").createdAt(now).updatedAt(now).build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findById(id))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldMapFindByIdErrorToPersistenceException() {
        UUID id = UUID.randomUUID();
        when(franchiseRepository.findById(id)).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.findById(id))
                .expectError(PersistenceException.class)
                .verify();
    }
}