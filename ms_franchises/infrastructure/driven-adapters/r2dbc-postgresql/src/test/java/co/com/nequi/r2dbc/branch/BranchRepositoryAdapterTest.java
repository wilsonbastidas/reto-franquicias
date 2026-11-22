package co.com.nequi.r2dbc.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.exceptions.ConflictException;
import co.com.nequi.r2dbc.entities.BranchEntity;
import co.com.nequi.r2dbc.mapper.BranchEntityMapper;
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
class BranchRepositoryAdapterTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BranchEntityMapper branchEntityMapper;

    @InjectMocks
    private BranchRepositoryAdapter adapter;

    @Test
    void shouldSaveBranchSuccessfully() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Franquicia").build();
        Branch branch = Branch.builder().name("Sucursal").franchise(franchise).build();
        BranchEntity entity = new BranchEntity();
        entity.setName("Sucursal");
        entity.setFranchiseId(franchiseId);
        BranchEntity savedEntity = new BranchEntity(branchId, "Sucursal", franchiseId, now, now);
        Branch mappedDomain = Branch.builder().id(branchId).name("Sucursal").createdAt(now).updatedAt(now).build();

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
        when(branchRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(branchRepository.save(any(BranchEntity.class))).thenReturn(Mono.just(savedEntity));
        when(branchEntityMapper.toDomain(savedEntity)).thenReturn(mappedDomain);

        StepVerifier.create(adapter.save(branch))
                .assertNext(result -> {
                    assert result.getId().equals(branchId);
                    assert result.getFranchise().getId().equals(franchiseId);
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowConflictWhenBranchAlreadyExists() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Franquicia").build();
        Branch branch = Branch.builder().name("Sucursal").franchise(franchise).build();
        BranchEntity entity = new BranchEntity();
        entity.setName("Sucursal");
        BranchEntity existing = new BranchEntity(UUID.randomUUID(), "Sucursal", franchiseId, LocalDateTime.now(), LocalDateTime.now());

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
        when(branchRepository.findByName("Sucursal")).thenReturn(Mono.just(existing));

        StepVerifier.create(adapter.save(branch))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldMapSaveErrorToPersistenceException() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Franquicia").build();
        Branch branch = Branch.builder().name("Sucursal").franchise(franchise).build();
        BranchEntity entity = new BranchEntity();
        entity.setName("Sucursal");

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
        when(branchRepository.findByName(anyString())).thenReturn(Mono.empty());
        when(branchRepository.save(any(BranchEntity.class))).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.save(branch))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        BranchEntity entity = new BranchEntity();
        entity.setId(id);
        Branch domain = Branch.builder().id(id).name("Sucursal").build();

        when(branchRepository.findById(id)).thenReturn(Mono.just(entity));
        when(branchEntityMapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findById(id))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldMapFindByIdErrorToPersistenceException() {
        UUID id = UUID.randomUUID();
        when(branchRepository.findById(id)).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.findById(id))
                .expectError(PersistenceException.class)
                .verify();
    }
}