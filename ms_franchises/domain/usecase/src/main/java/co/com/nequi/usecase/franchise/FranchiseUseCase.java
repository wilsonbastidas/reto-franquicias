package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepositoryGateway franchiseRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.findByName(franchise.getName())
                .hasElement()
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ConflictException("Franchise with name " + franchise.getName() + " already exists"));
                    }
                    return franchiseRepository.save(franchise);
                });
    }
}
