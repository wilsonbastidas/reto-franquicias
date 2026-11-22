package co.com.nequi.api;

import co.com.nequi.api.dto.BranchResponse;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.dto.ProductRequest;
import co.com.nequi.api.dto.ProductResponse;
import co.com.nequi.api.dto.StockProductRequest;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.handler.ProductHandler;
import co.com.nequi.api.mapper.ProductMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.product.Product;
import co.com.nequi.usecase.product.ProductUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ProductRouterRest.class, ProductHandler.class})
@WebFluxTest
class ProductRouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ProductUseCase productUseCase;

    @MockitoBean
    private ProductMapper productMapper;

    @MockitoBean
    private GlobalHandlerError globalHandlerError;

    @MockitoBean
    private ObjectValidator validator;

    @Test
    void shouldRouteUpdateStockProduct() {
        UUID productId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        StockProductRequest request = new StockProductRequest(productId, BigDecimal.TEN);
        Product domain = Product.builder().id(productId).stock(BigDecimal.TEN).build();
        Product updated = Product.builder().id(productId).name("Producto").stock(BigDecimal.TEN).build();
        BranchResponse branchResponse = new BranchResponse(branchId, "Sucursal", new FranchiseResponse(UUID.randomUUID().toString(), "Franquicia", now), now);
        ProductResponse response = new ProductResponse(productId, "Producto", BigDecimal.TEN, branchResponse);

        when(validator.validate(any(StockProductRequest.class))).thenReturn(Mono.just(request));
        when(productMapper.toDomainFromStockRequest(any(StockProductRequest.class))).thenReturn(domain);
        when(productUseCase.updateStockProduct(any(Product.class))).thenReturn(Mono.just(updated));
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        webTestClient.patch()
                .uri("/api/v1/products/update-stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(201)
                .jsonPath("$.data.stock").isEqualTo(10);
    }

    @Test
    void shouldRouteGetMaxStockProductsByFranchise() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Product product = Product.builder().id(productId).name("Producto").stock(BigDecimal.TEN)
                .branch(Branch.builder().id(branchId).name("Sucursal").build()).build();
        BranchResponse branchResponse = new BranchResponse(branchId, "Sucursal", new FranchiseResponse(franchiseId.toString(), "Franquicia", now), now);
        ProductResponse response = new ProductResponse(productId, "Producto", BigDecimal.TEN, branchResponse);

        when(validator.validateNotBlank(franchiseId.toString(), "franchiseId")).thenReturn(Mono.just(franchiseId.toString()));
        when(productUseCase.getMaxStockProductsByFranchise(franchiseId)).thenReturn(Flux.just(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        webTestClient.get()
                .uri("/api/v1/franchises/{franchiseId}/products/max-stock", franchiseId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(200)
                .jsonPath("$.data[0].name").isEqualTo("Producto");
    }

    @Test
    void shouldRouteDeleteProduct() {
        UUID productId = UUID.randomUUID();
        when(validator.validateNotBlank(productId.toString(), "id")).thenReturn(Mono.just(productId.toString()));
        when(productUseCase.deleteProduct(productId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/products/{id}", productId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldRouteCreateProduct() {
        UUID branchId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        ProductRequest request = new ProductRequest("Producto", BigDecimal.TEN, branchId.toString());
        Product domain = Product.builder().name("Producto").stock(BigDecimal.TEN).branch(Branch.builder().id(branchId).build()).build();
        Product saved = Product.builder().id(productId).name("Producto").stock(BigDecimal.TEN).branch(Branch.builder().id(branchId).name("Sucursal").build()).build();
        BranchResponse branchResponse = new BranchResponse(branchId, "Sucursal", new FranchiseResponse(UUID.randomUUID().toString(), "Franquicia", now), now);
        ProductResponse response = new ProductResponse(productId, "Producto", BigDecimal.TEN, branchResponse);

        when(validator.validate(any(ProductRequest.class))).thenReturn(Mono.just(request));
        when(productMapper.toDomain(any(ProductRequest.class))).thenReturn(domain);
        when(productUseCase.createProduct(any(Product.class))).thenReturn(Mono.just(saved));
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        webTestClient.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(201)
                .jsonPath("$.data.name").isEqualTo("Producto");
    }
}