package co.com.nequi.r2dbc.product;

import co.com.nequi.r2dbc.entities.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, UUID> {
    Mono<ProductEntity> findByName(String name);
}
