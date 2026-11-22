package co.com.nequi.r2dbc.product;

import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.entities.ProductEntity;
import co.com.nequi.r2dbc.mapper.ProductEntityMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public class ProductRepositoryAdapter implements ProductRepositoryGateway {

    private final ProductRepository productRepository;
    private final ProductEntityMapper mapper;

    public ProductRepositoryAdapter(ProductRepository productRepository, ProductEntityMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }


    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return productRepository.save(entity)
                .map(prod -> {
                    Product savedProduct = mapper.toDomain(prod);
                    savedProduct.setBranch(product.getBranch());
                    return savedProduct;
                })
                .onErrorMap(e -> new PersistenceException("Error saving product", e));
    }

    @Override
    public Mono<Product> findById(String id) {
        return null;
    }
}
