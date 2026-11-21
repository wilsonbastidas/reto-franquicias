package co.com.nequi.api.config;

import co.com.nequi.api.Handler;
import co.com.nequi.api.RouterRest;
import co.com.nequi.api.dto.FranchiseRequest;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.mapper.FranchiseMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase useCase;

    @MockitoBean
    private FranchiseMapper mapper;

    @MockitoBean
    private ObjectValidator validator;

    @MockitoBean
    private GlobalHandlerError globalHandlerError;

    @Test
    void securityHeadersShouldBePresentOnResponse() {
        FranchiseRequest request = new FranchiseRequest("Franquicia Centro");
        Franchise domain = Franchise.builder().name("Franquicia Centro").build();
        Franchise saved = Franchise.builder()
                .id(UUID.randomUUID())
                .name("Franquicia Centro")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        FranchiseResponse responseDTO = new FranchiseResponse(saved.getId().toString(), saved.getName(), saved.getCreatedAt());

        when(validator.validate(any(FranchiseRequest.class))).thenReturn(Mono.just(request));
        when(mapper.toDomain(any(FranchiseRequest.class))).thenReturn(domain);
        when(useCase.createFranchise(any(Franchise.class))).thenReturn(Mono.just(saved));
        when(mapper.toDTO(any(Franchise.class))).thenReturn(responseDTO);

        webTestClient.post()
                .uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().doesNotExist("Server")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}