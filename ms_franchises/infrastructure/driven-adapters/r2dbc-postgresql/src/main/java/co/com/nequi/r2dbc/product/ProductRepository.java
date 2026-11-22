package co.com.nequi.r2dbc.product;

import co.com.nequi.r2dbc.entities.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, String> {
}
