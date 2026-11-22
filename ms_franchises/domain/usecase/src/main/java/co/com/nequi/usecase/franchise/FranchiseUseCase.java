package co.com.nequi.usecase.franchise;

import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

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

    public Mono<Franchise> updateFranchiseName(UUID id, String name) {
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Franchise not found")))
                .flatMap(existing -> franchiseRepository.findByName(name)
                        .flatMap(found -> {
                            if (!found.getId().equals(id)) {
                                return Mono.error(new ConflictException("Franchise with name " + name + " already exists"));
                            }
                            return Mono.just(existing);
                        })
                        .defaultIfEmpty(existing)
                        .flatMap(franchise -> {
                            franchise.setName(name);
                            franchise.setUpdatedAt(LocalDateTime.now());
                            return franchiseRepository.update(franchise);
                        })
                );
    }
}
