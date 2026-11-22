package co.com.nequi.api.handler;

import co.com.nequi.api.dto.ProductRequest;
import co.com.nequi.api.error.GlobalHandlerError;
import co.com.nequi.api.mapper.ProductMapper;
import co.com.nequi.api.utils.ObjectValidator;
import co.com.nequi.api.utils.ResponseUtils;
import co.com.nequi.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;
    private final GlobalHandlerError globalHandlerError;
    private final ObjectValidator validator;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(ProductRequest.class)
                .flatMap(validator::validate)
                .map(productMapper::toDomain)
                .flatMap(productUseCase::createProduct)
                .map(productMapper::toResponse)
                .flatMap(response ->
                        ResponseUtils.buildCreatedResponse(response, "Product created successfully"))
                .onErrorResume(e -> globalHandlerError.renderError(request, e));
    }
}
