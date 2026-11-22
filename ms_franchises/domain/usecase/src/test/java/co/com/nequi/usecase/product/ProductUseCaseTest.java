package co.com.nequi.usecase.product;

import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.exceptions.NotFoundException;
import co.com.nequi.model.gateways.BranchRepositoryGateway;
import co.com.nequi.model.gateways.ProductRepositoryGateway;
import co.com.nequi.model.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @InjectMocks
    private ProductUseCase productUseCase;

    @Test
    void shouldCreateProductWhenBranchExists() {
        UUID branchId = UUID.randomUUID();
        Branch branch = Branch.builder().id(branchId).name("Sucursal").build();
        Product product = Product.builder().name("Producto").stock(BigDecimal.TEN).branch(Branch.builder().id(branchId).build()).build();
        Product saved = Product.builder().id(UUID.randomUUID()).name("Producto").stock(BigDecimal.TEN).branch(branch).build();

        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(productUseCase.createProduct(product))
                .expectNext(saved)
                .verifyComplete();
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
}