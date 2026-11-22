
package co.com.nequi.api.error;

import co.com.nequi.model.franchise.exceptions.ConflictException;
import co.com.nequi.model.franchise.exceptions.InvalidMethodException;
import co.com.nequi.model.franchise.exceptions.NotFoundException;
import co.com.nequi.model.franchise.exceptions.TechnicalException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalHandlerError implements ErrorWebExceptionHandler {
    private static final String GENERIC_ERROR = "An unexpected error occurred";
    private static final String TECHNICAL_ERROR = "A technical error occurred";
    private static final String INVALID_REQUEST = "Malformed or invalid request body";

    private final JsonMapper jsonMapper;

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        ErrorResponse error = resolveError(ex, exchange.getRequest().getMethod().name(), exchange.getRequest().getPath().value());
        response.setStatusCode(HttpStatusCode.valueOf(error.status()));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return Mono.fromCallable(() -> response.bufferFactory().wrap(
                        jsonMapper.writeValueAsBytes(error))
        ).flatMap(buffer -> response.writeWith(Mono.just(buffer)));
    }

    public Mono<ServerResponse> renderError(ServerRequest request, Throwable ex) {
        ErrorResponse error = resolveError(ex, request.method().name(), request.path());
        return ServerResponse
                .status(error.status())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(error);
    }

    /**
     * Resolves the relevant exception and builds the HTTP error response.
     */
    private ErrorResponse resolveError(Throwable ex, String method, String path) {
        Throwable cause = findRelevantCause(ex);
        ErrorResponse error = toErrorResponse(cause);
        logError(error, ex, cause, method, path);
        return error;
    }

    private Throwable findRelevantCause(Throwable ex) {
        Throwable current = ex;
        IdentityHashMap<Throwable, Boolean> visited = new IdentityHashMap<>();

        while (current != null && visited.put(current, Boolean.TRUE) == null) {
            if (isKnownException(current)) {
                return current;
            }
            current = current.getCause();
        }
        return ex;
    }

    private boolean isKnownException(Throwable ex) {
        return ex instanceof TechnicalException
                || ex instanceof ConflictException
                || ex instanceof NotFoundException
                || ex instanceof InvalidMethodException
                || ex instanceof ConstraintViolationException
                || ex instanceof WebExchangeBindException
                || ex instanceof ServerWebInputException
                || ex instanceof DecodingException
                || ex instanceof MethodNotAllowedException
                || ex instanceof ResponseStatusException
                || ex instanceof IllegalArgumentException;
    }

    private ErrorResponse toErrorResponse(Throwable cause) {
        return switch (cause) {
            case ConstraintViolationException ex ->
                    buildErrorResponse(constraintDetails(ex), HttpStatus.BAD_REQUEST);
            case WebExchangeBindException ex ->
                    buildErrorResponse(bindingDetails(ex), HttpStatus.BAD_REQUEST);
            case ServerWebInputException ex ->
                    buildErrorResponse(Map.of("error", INVALID_REQUEST), HttpStatus.BAD_REQUEST);
            case DecodingException ex ->
                    buildErrorResponse( Map.of("error", INVALID_REQUEST), HttpStatus.BAD_REQUEST);
            case IllegalArgumentException ex ->
                    buildErrorResponse(Map.of("error", safeMessage(ex.getMessage(), "Invalid request")),
                            HttpStatus.BAD_REQUEST
                    );
            case InvalidMethodException ex ->
                    buildErrorResponse(Map.of("error", safeMessage(ex.getMessage(),  "Method not allowed" )),
                            HttpStatus.METHOD_NOT_ALLOWED
                    );
            case MethodNotAllowedException ex ->
                    buildErrorResponse(Map.of("error", "HTTP method not allowed for the requested route"),
                            HttpStatus.METHOD_NOT_ALLOWED
                    );
            case ResponseStatusException ex ->
                    buildErrorResponse(Map.of("error", safeMessage(ex.getReason(),"Request failed")),
                            ex.getStatusCode()
                    );
            case ConflictException ex ->
                    buildErrorResponse(Map.of("error", safeMessage(ex.getMessage(), "Conflict")), HttpStatus.CONFLICT);
            case NotFoundException ex ->
                    buildErrorResponse(Map.of("error", safeMessage(ex.getMessage(), "Resource not found")), HttpStatus.NOT_FOUND);
            case TechnicalException ex ->
                    buildErrorResponse(Map.of("error", safeMessage(ex.getMessage(), TECHNICAL_ERROR)), HttpStatus.INTERNAL_SERVER_ERROR);
            default -> buildErrorResponse(Map.of("error", GENERIC_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    private Map<String, String> constraintDetails(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(violation ->
                                violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));
    }

    private Map<String, String> bindingDetails(WebExchangeBindException ex) {
        return ex.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(error ->
                                error.getField(),
                        error -> safeMessage(error.getDefaultMessage(),"Invalid value"),
                        (existing, replacement) -> existing
                ));
    }

    private ErrorResponse buildErrorResponse(Map<String, String> details, HttpStatusCode status) {
        return new ErrorResponse(status.value(), LocalDateTime.now(), details);
    }

    private void logError(ErrorResponse error, Throwable original,
            Throwable cause, String method, String path
    ) {
        if (error.status() >= HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            log.error("Unhandled error processing {} {}", method, path, original);
        } else {
            log.warn("Request error processing {} {}: {}", method, path, cause.getMessage());
        }
    }

    private String safeMessage(String message, String fallback) {
        return message == null || message.isBlank() ? fallback : message;
    }
}
