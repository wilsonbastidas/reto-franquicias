package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.franchise.Franchise;
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

        when(franchiseRepository.findByName("Franquicia")).thenReturn(Mono.empty());
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void createFranchiseShouldReturnConflictWhenNameExists() {
        Franchise franchise = Franchise.builder()
                .name("Franquicia")
                .build();

        when(franchiseRepository.findByName("Franquicia")).thenReturn(Mono.just(Franchise.builder().id(UUID.randomUUID()).name("Franquicia").build()));

        StepVerifier.create(useCase.createFranchise(franchise))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldUpdateFranchiseNameWhenExists() {
        UUID id = UUID.randomUUID();
        Franchise existing = Franchise.builder().id(id).name("Old").build();
        Franchise updated = Franchise.builder().id(id).name("New").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existing));
        when(franchiseRepository.findByName("New")).thenReturn(Mono.empty());
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateFranchiseName(id, "New"))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingFranchiseName() {
        UUID id = UUID.randomUUID();
        when(franchiseRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateFranchiseName(id, "New"))
                .expectError(co.com.nequi.model.exceptions.NotFoundException.class)
                .verify();
    }

    @Test
    void shouldReturnConflictWhenUpdatingFranchiseNameExists() {
        UUID id = UUID.randomUUID();
        Franchise existing = Franchise.builder().id(id).name("Old").build();
        Franchise other = Franchise.builder().id(UUID.randomUUID()).name("New").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existing));
        when(franchiseRepository.findByName("New")).thenReturn(Mono.just(other));

        StepVerifier.create(useCase.updateFranchiseName(id, "New"))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldUpdateFranchiseNameWhenSameNameBelongsToSameFranchise() {
        UUID id = UUID.randomUUID();
        Franchise existing = Franchise.builder().id(id).name("Same").build();
        Franchise updated = Franchise.builder().id(id).name("Same").build();

        when(franchiseRepository.findById(id)).thenReturn(Mono.just(existing));
        when(franchiseRepository.findByName("Same")).thenReturn(Mono.just(existing));
        when(franchiseRepository.update(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateFranchiseName(id, "Same"))
                .expectNext(updated)
                .verifyComplete();
    }
}