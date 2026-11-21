package co.com.nequi.api;

import co.com.nequi.api.dto.FranchiseRequest;
import co.com.nequi.api.dto.FranchiseResponse;
import co.com.nequi.api.dto.ResponseGenericDTO;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.mapper.FranchiseMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final FranchiseUseCase useCase;
    private final FranchiseMapper mapper;
    private final ObjectValidator validator;
    private final GlobalHandlerError globalHandlerError;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequest.class)
                .flatMap(validator::validate)
                .map(mapper::toDomain)
                .flatMap(useCase::createFranchise)
                .map(mapper::toDTO)
                .flatMap(this::buildCreatedResponse)
                .onErrorResume(e -> globalHandlerError.renderError(request, e));
    }

    private Mono<ServerResponse> buildCreatedResponse(FranchiseResponse franchiseResponse) {
        ResponseGenericDTO<FranchiseResponse> body = new ResponseGenericDTO<>(
                HttpStatus.CREATED.value(),
                "Franchise created successfully",
                franchiseResponse,
                null
        );
        return ServerResponse.status(HttpStatus.CREATED).bodyValue(body);
    }
}
