package co.com.nequi.r2dbc.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.entities.ProductEntity;
import co.com.nequi.r2dbc.mapper.ProductEntityMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEntityMapper mapper;

    @InjectMocks
    private ProductRepositoryAdapter adapter;

    @Test
    void shouldSaveProductSuccessfully() {
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product product = Product.builder().name("Producto").stock(BigDecimal.TEN).branch(branch).build();
        ProductEntity entity = new ProductEntity();
        entity.setName("Producto");
        entity.setStock(BigDecimal.TEN);
        entity.setBranchId(branchId);
        ProductEntity savedEntity = new ProductEntity(productId, "Producto", BigDecimal.TEN, branchId, now, now);
        Product mappedDomain = Product.builder().id(productId).name("Producto").stock(BigDecimal.TEN).build();

        when(mapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.toDomain(savedEntity)).thenReturn(mappedDomain);

        StepVerifier.create(adapter.save(product))
                .assertNext(result -> {
                    assert result.getId().equals(productId);
                    assert result.getBranch().getId().equals(branchId);
                })
                .verifyComplete();
    }

    @Test
    void shouldMapSaveErrorToPersistenceException() {
        UUID branchId = UUID.randomUUID();
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product product = Product.builder().name("Producto").stock(BigDecimal.TEN).branch(branch).build();
        ProductEntity entity = new ProductEntity();

        when(mapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.save(product))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldFindById() {
        UUID id = UUID.randomUUID();
        ProductEntity entity = new ProductEntity();
        entity.setId(id);
        Product domain = Product.builder().id(id).name("Producto").build();

        when(productRepository.findById(id)).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findById(id))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldMapFindByIdErrorToPersistenceException() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.findById(id))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldDeleteById() {
        UUID id = UUID.randomUUID();
        when(productRepository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(id))
                .verifyComplete();
    }

    @Test
    void shouldMapDeleteByIdErrorToPersistenceException() {
        UUID id = UUID.randomUUID();
        when(productRepository.deleteById(id)).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.deleteById(id))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldFindByName() {
        ProductEntity entity = new ProductEntity();
        entity.setName("Producto");
        Product domain = Product.builder().name("Producto").build();

        when(productRepository.findByName("Producto")).thenReturn(Mono.just(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findByName("Producto"))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldMapFindByNameErrorToPersistenceException() {
        when(productRepository.findByName("Producto")).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.findByName("Producto"))
                .expectError(PersistenceException.class)
                .verify();
    }
}