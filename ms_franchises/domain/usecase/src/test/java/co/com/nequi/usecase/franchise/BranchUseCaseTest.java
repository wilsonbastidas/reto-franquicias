package co.com.nequi.usecase.franchise;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import co.com.nequi.usecase.branch.BranchUseCase;
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
}