package co.com.nequi.model.gateways;

import co.com.nequi.model.product.Product;
import reactor.core.publisher.Mono;

public interface ProductRepositoryGateway {

    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
}
