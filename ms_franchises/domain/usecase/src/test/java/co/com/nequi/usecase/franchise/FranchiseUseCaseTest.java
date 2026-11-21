package co.com.nequi.usecase.franchise;

import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.franchise.gateways.FranchiseRepositoryGateway;
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
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepositoryGateway franchiseRepository;

    @InjectMocks
    private FranchiseUseCase useCase;

    @Test
    void createFranchiseShouldDelegateToRepository() {
        Franchise franchise = Franchise.builder()
                .id(UUID.randomUUID())
                .name("Franquicia")
                .build();

        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }
}