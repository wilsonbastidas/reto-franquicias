package co.com.nequi.r2dbc.product;

import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.branch.BranchRepository;
import co.com.nequi.r2dbc.entities.ProductEntity;
import co.com.nequi.r2dbc.mapper.BranchEntityMapper;
import co.com.nequi.r2dbc.mapper.ProductEntityMapper;
import co.com.nequi.resiliencehelper.ReactiveResilienceHelper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class ProductRepositoryAdapter implements ProductRepositoryGateway {

    private final ProductRepository productRepository;
    private final ProductEntityMapper mapper;
    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;
    private final ReactiveResilienceHelper resilienceHelper;

    public ProductRepositoryAdapter(ProductRepository productRepository, ProductEntityMapper mapper,
                                    BranchRepository branchRepository, BranchEntityMapper branchEntityMapper,
                                    ReactiveResilienceHelper resilienceHelper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
        this.branchRepository = branchRepository;
        this.branchEntityMapper = branchEntityMapper;
        this.resilienceHelper = resilienceHelper;
    }


    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        return resilienceHelper.protectMono(
                productRepository.save(entity)
                        .map(prod -> {
                            Product savedProduct = mapper.toDomain(prod);
                            savedProduct.setBranch(product.getBranch());
                            return savedProduct;
                        }),
                "product-save"
        ).onErrorMap(e -> new PersistenceException("Error saving product", e));
    }

    @Override
    public Mono<Product> findById(UUID id) {
        return resilienceHelper.protectMono(
                productRepository.findById(id)
                        .map(mapper::toDomain),
                "product-findById"
        ).onErrorMap(e -> new PersistenceException("Error finding product", e));
    }

    @Override
    public Mono<Product> findByName(String name) {
        return resilienceHelper.protectMono(
                productRepository.findByName(name)
                        .map(mapper::toDomain),
                "product-findByName"
        ).onErrorMap(e -> new PersistenceException("Error finding product", e));
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return resilienceHelper.protectMono(
                productRepository.deleteById(id),
                "product-deleteById"
        ).onErrorMap(e -> new PersistenceException("Error deleting product", e));
    }

    @Override
    public Mono<Product> update(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        entity.setBranchId(product.getBranch().getId());
        return resilienceHelper.protectMono(
                productRepository.save(entity)
                        .map(mapper::toDomain),
                "product-update"
        ).onErrorMap(e -> new PersistenceException("Error updating product", e));
    }

    @Override
    public Flux<Product> findMaxStockByFranchiseId(UUID franchiseId) {
        return resilienceHelper.protectFlux(
                productRepository.findMaxStockByFranchiseId(franchiseId)
                        .flatMap(entity -> resilienceHelper.protectMono(
                                branchRepository.findById(entity.getBranchId())
                                        .map(branchEntityMapper::toDomain),
                                "product-findBranchById"
                        ).map(branch -> {
                            Product product = mapper.toDomain(entity);
                            product.setBranch(branch);
                            return product;
                        })),
                "product-findMaxStockByFranchiseId"
        ).onErrorMap(e -> new PersistenceException("Error finding max stock products", e));
    }
}