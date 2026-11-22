package co.com.nequi.usecase.branch;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private BranchRepositoryGateway branchRepository;

    @Mock
    private FranchiseRepositoryGateway franchiseRepository;

    @InjectMocks
    private BranchUseCase branchUseCase;

    @Test
    void shouldCreateBranchWhenFranchiseExists() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Franquicia").build();
        Branch branch = Branch.builder().name("Sucursal").franchise(Franchise.builder().id(franchiseId).build()).build();
        Branch saved = Branch.builder().id(UUID.randomUUID()).name("Sucursal").franchise(franchise).build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByName("Sucursal")).thenReturn(Mono.empty());
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(branchUseCase.createBranch(branch))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundWhenFranchiseDoesNotExist() {
        UUID franchiseId = UUID.randomUUID();
        Branch branch = Branch.builder().name("Sucursal").franchise(Franchise.builder().id(franchiseId).build()).build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.createBranch(branch))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnConflictWhenBranchNameAlreadyExists() {
        UUID franchiseId = UUID.randomUUID();
        Franchise franchise = Franchise.builder().id(franchiseId).name("Franquicia").build();
        Branch branch = Branch.builder().name("Sucursal").franchise(Franchise.builder().id(franchiseId).build()).build();
        Branch existing = Branch.builder().id(UUID.randomUUID()).name("Sucursal").build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(franchise));
        when(branchRepository.findByName("Sucursal")).thenReturn(Mono.just(existing));

        StepVerifier.create(branchUseCase.createBranch(branch))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldUpdateBranchNameWhenExists() {
        UUID id = UUID.randomUUID();
        Branch existing = Branch.builder().id(id).name("Old").build();
        Branch updated = Branch.builder().id(id).name("New").build();

        when(branchRepository.findById(id)).thenReturn(Mono.just(existing));
        when(branchRepository.findByName("New")).thenReturn(Mono.empty());
        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(branchUseCase.updateBranchName(id, "New"))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingBranchName() {
        UUID id = UUID.randomUUID();
        when(branchRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateBranchName(id, "New"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnConflictWhenUpdatingBranchNameExists() {
        UUID id = UUID.randomUUID();
        Branch existing = Branch.builder().id(id).name("Old").build();
        Branch other = Branch.builder().id(UUID.randomUUID()).name("New").build();

        when(branchRepository.findById(id)).thenReturn(Mono.just(existing));
        when(branchRepository.findByName("New")).thenReturn(Mono.just(other));

        StepVerifier.create(branchUseCase.updateBranchName(id, "New"))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldUpdateBranchNameWhenSameNameBelongsToSameBranch() {
        UUID id = UUID.randomUUID();
        Branch existing = Branch.builder().id(id).name("Same").build();
        Branch updated = Branch.builder().id(id).name("Same").build();

        when(branchRepository.findById(id)).thenReturn(Mono.just(existing));
        when(branchRepository.findByName("Same")).thenReturn(Mono.just(existing));
        when(branchRepository.update(any(Branch.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(branchUseCase.updateBranchName(id, "Same"))
                .expectNext(updated)
                .verifyComplete();
    }
}