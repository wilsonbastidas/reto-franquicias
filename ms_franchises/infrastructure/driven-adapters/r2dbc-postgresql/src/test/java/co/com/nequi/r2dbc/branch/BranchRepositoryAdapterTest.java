package co.com.nequi.r2dbc.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.franchise.Franchise;
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
    void shouldMapSaveErrorToPersistenceException() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Franquicia").build();
        Branch branch = Branch.builder().name("Sucursal").franchise(franchise).build();
        BranchEntity entity = new BranchEntity();
        entity.setName("Sucursal");

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
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

    @Test
    void shouldFindByName() {
        BranchEntity entity = new BranchEntity();
        entity.setName("Sucursal");
        Branch domain = Branch.builder().name("Sucursal").build();

        when(branchRepository.findByName("Sucursal")).thenReturn(Mono.just(entity));
        when(branchEntityMapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findByName("Sucursal"))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldMapFindByNameErrorToPersistenceException() {
        when(branchRepository.findByName("Sucursal")).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.findByName("Sucursal"))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldUpdateBranchSuccessfully() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Franquicia").build();
        Branch branch = Branch.builder().id(branchId).name("New").franchise(franchise).build();
        BranchEntity entity = new BranchEntity();
        entity.setId(branchId);
        entity.setName("New");
        BranchEntity savedEntity = new BranchEntity(branchId, "New", franchiseId, now, now);
        Branch mapped = Branch.builder().id(branchId).name("New").build();

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
        when(branchRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(branchEntityMapper.toDomain(savedEntity)).thenReturn(mapped);

        StepVerifier.create(adapter.update(branch))
                .assertNext(result -> {
                    assert result.getName().equals("New");
                    assert result.getFranchise().getId().equals(franchiseId);
                })
                .verifyComplete();
    }

    @Test
    void shouldMapUpdateErrorToPersistenceException() {
        Branch branch = Branch.builder().id(UUID.randomUUID()).name("New").build();
        BranchEntity entity = new BranchEntity();

        when(branchEntityMapper.toEntity(branch)).thenReturn(entity);
        when(branchRepository.save(entity)).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.update(branch))
                .expectError(PersistenceException.class)
                .verify();
    }
}
