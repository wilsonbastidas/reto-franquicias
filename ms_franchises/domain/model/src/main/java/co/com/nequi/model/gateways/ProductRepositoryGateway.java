package co.com.nequi.model.gateways;

import co.com.nequi.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductRepositoryGateway {

    Mono<Product> save(Product product);
    Mono<Product> findById(UUID id);
    Mono<Product> findByName(String name);
    Mono<Void> deleteById(UUID id);
    Mono<Product> update(Product product);
    Flux<Product> findMaxStockByFranchiseId(UUID franchiseId);
}
