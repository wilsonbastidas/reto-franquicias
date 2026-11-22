package co.com.nequi.api.handler;

import co.com.nequi.api.dto.BranchResponse;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.dto.ProductRequest;
import co.com.nequi.api.dto.ProductResponse;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.mapper.ProductMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.product.Product;
import co.com.nequi.usecase.product.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@ExtendWith(MockitoExtension.class)
class ProductHandlerTest {

    @Mock
    private ProductUseCase productUseCase;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private GlobalHandlerError globalHandlerError;
    @Mock
    private ObjectValidator validator;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ProductHandler handler = new ProductHandler(productUseCase, productMapper, globalHandlerError, validator);
        RouterFunction<ServerResponse> routes = route(POST("/api/v1/products"), handler::createProduct)
                .andRoute(DELETE("/api/v1/products/{id}"), handler::deleteProductById);
        webTestClient = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void shouldDeleteProductById() {
        UUID productId = UUID.randomUUID();
        when(validator.validateNotBlank(productId.toString(), "id")).thenReturn(Mono.just(productId.toString()));
        when(productUseCase.deleteProduct(productId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/products/{id}", productId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldReturnErrorWhenDeleteIdIsInvalid() {
        when(validator.validateNotBlank(any(), any())).thenReturn(Mono.error(new IllegalArgumentException("id cannot be null or empty")));
        when(globalHandlerError.renderError(any(), any())).thenReturn(ServerResponse.badRequest().build());

        webTestClient.delete()
                .uri("/api/v1/products/{id}", "bad")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldCreateProduct() {
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
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(201)
                .jsonPath("$.message").isEqualTo("Product created successfully")
                .jsonPath("$.data.name").isEqualTo("Producto");
    }
}