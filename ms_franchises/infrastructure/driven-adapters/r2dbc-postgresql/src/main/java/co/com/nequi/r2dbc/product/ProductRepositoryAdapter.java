package co.com.nequi.r2dbc.product;

import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.entities.ProductEntity;
import co.com.nequi.r2dbc.mapper.ProductEntityMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

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
    public Mono<Product> findById(UUID id) {
        return productRepository.findById(id)
                .map(mapper::toDomain)
                .onErrorMap(e -> new PersistenceException("Error finding product", e));
    }

    @Override
    public Mono<Product> findByName(String name) {
        return productRepository.findByName(name)
                .map(mapper::toDomain)
                .onErrorMap(e -> new PersistenceException("Error finding product", e));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return productRepository.deleteById(id)
                .onErrorMap(e -> new PersistenceException("Error deleting product", e));
    }
}
