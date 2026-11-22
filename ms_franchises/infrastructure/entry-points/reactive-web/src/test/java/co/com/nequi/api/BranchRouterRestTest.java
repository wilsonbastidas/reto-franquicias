package co.com.nequi.api;

import co.com.nequi.api.dto.BranchRequest;
import co.com.nequi.api.dto.BranchResponse;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.handler.BranchHandler;
import co.com.nequi.api.mapper.BranchMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.franchise.BranchUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {BranchRouterRest.class, BranchHandler.class})
@WebFluxTest
class BranchRouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BranchUseCase branchUseCase;

    @MockitoBean
    private BranchMapper mapper;

    @MockitoBean
    private ObjectValidator validator;

    @MockitoBean
    private GlobalHandlerError globalHandlerError;

    @Test
    void shouldRouteCreateBranch() {
        UUID franchiseId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        BranchRequest request = new BranchRequest("Sucursal", franchiseId);
        Branch domain = Branch.builder().name("Sucursal").franchise(Franchise.builder().id(franchiseId).build()).build();
        Branch saved = Branch.builder().id(branchId).name("Sucursal").franchise(Franchise.builder().id(franchiseId).name("Franquicia").build()).createdAt(now).build();
        BranchResponse response = new BranchResponse(branchId, "Sucursal", new FranchiseResponse(franchiseId.toString(), "Franquicia", now), now);

        when(validator.validate(any(BranchRequest.class))).thenReturn(Mono.just(request));
        when(mapper.toCreateDomain(any(BranchRequest.class))).thenReturn(domain);
        when(branchUseCase.createBranch(any(Branch.class))).thenReturn(Mono.just(saved));
        when(mapper.toResponse(any(Branch.class))).thenReturn(response);

        webTestClient.post()
                .uri("/api/v1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(201)
                .jsonPath("$.data.name").isEqualTo("Sucursal");
    }
}