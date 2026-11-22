package co.com.nequi.api;

import co.com.nequi.api.dto.BranchResponse;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.dto.ProductRequest;
import co.com.nequi.api.dto.ProductResponse;
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