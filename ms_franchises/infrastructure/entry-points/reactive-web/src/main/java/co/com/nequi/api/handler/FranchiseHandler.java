package co.com.nequi.api.handler;

import co.com.nequi.api.dto.FranchiseRequest;
import co.com.nequi.api.dto.UpdateFranchiseNameRequest;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.mapper.FranchiseMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.api.utils.ResponseUtils;
import co.com.nequi.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

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
                .flatMap(response ->
                        ResponseUtils.buildCreatedResponse(response, "Franchise created successfully"))
                .onErrorResume(e -> globalHandlerError.renderError(request, e));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        return request.bodyToMono(UpdateFranchiseNameRequest.class)
                .flatMap(validator::validate)
                .flatMap(req -> useCase.updateFranchiseName(req.id(), req.name()))
                .map(mapper::toDTO)
                .flatMap(response ->
                        ResponseUtils.buildOkResponse(response, "Franchise name updated successfully"))
                .onErrorResume(e -> globalHandlerError.renderError(request, e));
    }
}
