package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.FranchiseRepositoryGateway;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepositoryGateway productRepository;

    @Mock
    private BranchRepositoryGateway branchRepository;

    @Mock
    private FranchiseRepositoryGateway franchiseRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    @Test
    void shouldCreateProductWhenBranchExists() {
        UUID branchId = UUID.randomUUID();
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product product = Product.builder().name("Producto").stock(BigDecimal.TEN).branch(Branch.builder().id(branchId).build()).build();
        Product saved = Product.builder().id(UUID.randomUUID()).name("Producto").stock(BigDecimal.TEN).branch(branch).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByName("Producto")).thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(productUseCase.createProduct(product))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void shouldReturnConflictWhenProductNameAlreadyExists() {
        UUID branchId = UUID.randomUUID();
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product product = Product.builder().name("Producto").stock(BigDecimal.TEN).branch(Branch.builder().id(branchId).build()).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.findByName("Producto")).thenReturn(Mono.just(Product.builder().id(UUID.randomUUID()).name("Producto").build()));

        StepVerifier.create(productUseCase.createProduct(product))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void shouldReturnNotFoundWhenBranchDoesNotExist() {
        UUID branchId = UUID.randomUUID();
        Product product = Product.builder().name("Producto").stock(BigDecimal.TEN).branch(Branch.builder().id(branchId).build()).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.createProduct(product))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldDeleteProductWhenExists() {
        UUID productId = UUID.randomUUID();
        Product product = Product.builder().id(productId).name("Producto").build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProduct(productId))
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistingProduct() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteProduct(productId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldGetMaxStockProductsByFranchise() {
        UUID franchiseId = UUID.randomUUID();
        Product product = Product.builder().id(UUID.randomUUID()).name("Producto").stock(BigDecimal.TEN).build();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.just(co.com.nequi.model.franchise.Franchise.builder().id(franchiseId).name("Franquicia").build()));
        when(productRepository.findMaxStockByFranchiseId(franchiseId)).thenReturn(Flux.just(product));

        StepVerifier.create(productUseCase.getMaxStockProductsByFranchise(franchiseId))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundWhenFranchiseDoesNotExistForMaxStock() {
        UUID franchiseId = UUID.randomUUID();

        when(franchiseRepository.findById(franchiseId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.getMaxStockProductsByFranchise(franchiseId))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void shouldUpdateStockProductWhenExists() {
        UUID productId = UUID.randomUUID();
        Product existing = Product.builder().id(productId).name("Producto").stock(BigDecimal.ONE).build();
        Product request = Product.builder().id(productId).stock(BigDecimal.TEN).build();
        Product updated = Product.builder().id(productId).name("Producto").stock(BigDecimal.TEN).build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existing));
        when(productRepository.update(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productUseCase.updateStockProduct(request))
                .expectNext(updated)
                .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistingProduct() {
        UUID productId = UUID.randomUUID();
        Product request = Product.builder().id(productId).stock(BigDecimal.TEN).build();

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStockProduct(request))
                .expectError(NotFoundException.class)
                .verify();
    }
}