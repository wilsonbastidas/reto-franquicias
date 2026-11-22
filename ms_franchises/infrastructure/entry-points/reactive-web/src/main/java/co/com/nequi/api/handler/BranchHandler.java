package co.com.nequi.api.handler;

import co.com.nequi.api.dto.BranchRequest;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.mapper.BranchMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.api.utils.ResponseUtils;
import co.com.nequi.usecase.franchise.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final BranchUseCase branchUseCase;
    private final BranchMapper mapper;
    private final ObjectValidator validator;
    private final GlobalHandlerError globalHandlerError;

    public Mono<ServerResponse> createBranch(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BranchRequest.class)
                .flatMap(validator::validate)
                .map(mapper::toCreateDomain)
                .flatMap(branchUseCase::createBranch)
                .map(mapper::toResponse)
                .flatMap(response ->
                        ResponseUtils.buildCreatedResponse(response, "Branch created successfully"))
                .onErrorResume(e -> globalHandlerError.renderError(serverRequest, e));
    }
}
