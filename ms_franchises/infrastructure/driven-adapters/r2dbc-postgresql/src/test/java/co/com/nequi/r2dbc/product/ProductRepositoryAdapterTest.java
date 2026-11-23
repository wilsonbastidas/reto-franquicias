package co.com.nequi.r2dbc.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.product.Product;
import co.com.nequi.r2dbc.branch.BranchRepository;
import co.com.nequi.r2dbc.entities.BranchEntity;
import co.com.nequi.r2dbc.entities.ProductEntity;
import co.com.nequi.r2dbc.mapper.BranchEntityMapper;
import co.com.nequi.r2dbc.mapper.ProductEntityMapper;
import co.com.nequi.resiliencehelper.ReactiveResilienceHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductEntityMapper mapper;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BranchEntityMapper branchEntityMapper;

    @Mock
    private ReactiveResilienceHelper resilienceHelper;

    @InjectMocks
    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        lenient().when(resilienceHelper.protectMono(any(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(resilienceHelper.protectFlux(any(), anyString()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

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

    @Test
    void shouldFindMaxStockByFranchiseId() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        ProductEntity entity = new ProductEntity();
        entity.setId(UUID.randomUUID());
        entity.setName("Producto");
        entity.setStock(BigDecimal.TEN);
        entity.setBranchId(branchId);
        BranchEntity branchEntity = new BranchEntity();
        branchEntity.setId(branchId);
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product domain = Product.builder().id(entity.getId()).name("Producto").stock(BigDecimal.TEN).build();

        when(productRepository.findMaxStockByFranchiseId(franchiseId)).thenReturn(Flux.just(entity));
        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branchEntity));
        when(branchEntityMapper.toDomain(branchEntity)).thenReturn(branch);
        when(mapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findMaxStockByFranchiseId(franchiseId))
                .assertNext(result -> {
                    assert result.getName().equals("Producto");
                    assert result.getBranch().getId().equals(branchId);
                })
                .verifyComplete();
    }

    @Test
    void shouldMapFindMaxStockByFranchiseIdErrorToPersistenceException() {
        UUID franchiseId = UUID.randomUUID();
        when(productRepository.findMaxStockByFranchiseId(franchiseId)).thenReturn(Flux.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.findMaxStockByFranchiseId(franchiseId))
                .expectError(PersistenceException.class)
                .verify();
    }

    @Test
    void shouldUpdateProductSuccessfully() {
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product product = Product.builder().id(productId).name("Producto").stock(BigDecimal.TEN).branch(branch).build();
        ProductEntity entity = new ProductEntity();
        entity.setId(productId);
        entity.setName("Producto");
        ProductEntity savedEntity = new ProductEntity();
        savedEntity.setId(productId);
        savedEntity.setName("Producto");
        Product updatedDomain = Product.builder().id(productId).name("Producto").stock(BigDecimal.TEN).build();

        when(mapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(Mono.just(savedEntity));
        when(mapper.toDomain(savedEntity)).thenReturn(updatedDomain);

        StepVerifier.create(adapter.update(product))
                .expectNext(updatedDomain)
                .verifyComplete();
    }

    @Test
    void shouldMapUpdateErrorToPersistenceException() {
        UUID branchId = UUID.randomUUID();
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product product = Product.builder().id(UUID.randomUUID()).name("Producto").stock(BigDecimal.TEN).branch(branch).build();
        ProductEntity entity = new ProductEntity();

        when(mapper.toEntity(product)).thenReturn(entity);
        when(productRepository.save(any(ProductEntity.class))).thenReturn(Mono.error(new RuntimeException("db error")));

        StepVerifier.create(adapter.update(product))
                .expectError(PersistenceException.class)
                .verify();
    }
}