package co.com.nequi.api.handler;

import co.com.nequi.api.dto.BranchRequest;
import co.com.nequi.api.dto.BranchResponse;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.mapper.BranchMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.model.branch.Branch;
import co.com.nequi.model.franchise.Franchise;
import co.com.nequi.usecase.branch.BranchUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @Mock
    private BranchUseCase branchUseCase;
    @Mock
    private BranchMapper mapper;
    @Mock
    private ObjectValidator validator;
    @Mock
    private GlobalHandlerError globalHandlerError;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        BranchHandler handler = new BranchHandler(branchUseCase, mapper, validator, globalHandlerError);
        RouterFunction<ServerResponse> routes = route(POST("/api/v1/branches"), handler::createBranch);
        webTestClient = WebTestClient.bindToRouterFunction(routes).build();
    }

    @Test
    void shouldCreateBranch() {
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
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(201)
                .jsonPath("$.message").isEqualTo("Branch created successfully")
                .jsonPath("$.data.name").isEqualTo("Sucursal")
                .jsonPath("$.data.franchise.name").isEqualTo("Franquicia");
    }
}