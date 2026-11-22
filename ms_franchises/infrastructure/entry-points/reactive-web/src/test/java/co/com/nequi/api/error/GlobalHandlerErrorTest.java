package co.com.nequi.api.error;

import co.com.nequi.model.exceptions.PersistenceException;
import co.com.nequi.model.exceptions.ConflictException;
import co.com.nequi.model.franchise.exceptions.InvalidMethodException;
import co.com.nequi.model.franchise.exceptions.NotFoundException;
import co.com.nequi.model.franchise.exceptions.TechnicalException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalHandlerErrorTest {

    private GlobalHandlerError globalHandlerError;

    @BeforeEach
    void setUp() {
        globalHandlerError = new GlobalHandlerError(JsonMapper.builder().build());
    }

    @Test
    void shouldRenderTechnicalExceptionAsControlled500() {
        ServerRequest request = postRequest();
        StepVerifier.create(globalHandlerError.renderError(request,
                        new PersistenceException("Error saving franchise", new RuntimeException("db down"))))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(500))
                .verifyComplete();
    }

    @Test
    void shouldRenderBadRequestWhenInputIsInvalid() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(), new ServerWebInputException("bad body")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldRenderBadRequestForDecodingException() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(), new DecodingException("bad json")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldRenderBadRequestForIllegalArgumentException() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(), new IllegalArgumentException("bad arg")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldRenderBadRequestForIllegalArgumentExceptionWithBlankMessage() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(), new IllegalArgumentException(" ")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldRenderMethodNotAllowedForInvalidMethodException() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(), new InvalidMethodException()))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(405))
                .verifyComplete();
    }

    @Test
    void shouldRenderMethodNotAllowedForSpringMethodNotAllowed() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(),
                        new MethodNotAllowedException("POST", List.of(HttpMethod.GET))))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(405))
                .verifyComplete();
    }

    @Test
    void shouldRenderResponseStatusException() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(),
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "not found")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(404))
                .verifyComplete();
    }

    @Test
    void shouldRenderConflictException() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(),
                        new ConflictException("Franchise with name X already exists")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(409))
                .verifyComplete();
    }

    @Test
    void shouldRenderNotFoundException() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(),
                        new NotFoundException("Franchise not found")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(404))
                .verifyComplete();
    }

    @Test
    void shouldRenderDefaultError() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(), new RuntimeException("boom")))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(500))
                .verifyComplete();
    }

    @Test
    void shouldRenderConstraintViolationDetails() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("name");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be blank");

        StepVerifier.create(globalHandlerError.renderError(postRequest(),
                        new ConstraintViolationException(Set.of(violation))))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldHandleExchangeError() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/franchise"));

        StepVerifier.create(globalHandlerError.handle(exchange, new TechnicalException("tech error")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldReturnErrorWhenResponseCommitted() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/franchise"));
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().setComplete().block();

        RuntimeException ex = new RuntimeException("boom");
        StepVerifier.create(globalHandlerError.handle(exchange, ex))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void shouldFindRelevantCauseInChain() {
        RuntimeException wrapped = new RuntimeException("wrapper",
                new PersistenceException("db error", new RuntimeException("root")));

        StepVerifier.create(globalHandlerError.renderError(postRequest(), wrapped))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(500))
                .verifyComplete();
    }

    @Test
    void shouldReturnLastCauseWhenNoKnownExceptionFound() {
        RuntimeException wrapped = new RuntimeException("wrapper", new IllegalStateException("root"));

        StepVerifier.create(globalHandlerError.renderError(postRequest(), wrapped))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(500))
                .verifyComplete();
    }

    @Test
    void shouldHandle4xxOnExchange() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.post("/api/v1/franchise"));

        StepVerifier.create(globalHandlerError.handle(exchange, new ServerWebInputException("bad body")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldMergeDuplicateConstraintViolations() {
        ConstraintViolation<?> v1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> v2 = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("name");
        when(v1.getPropertyPath()).thenReturn(path);
        when(v1.getMessage()).thenReturn("must not be blank");
        when(v2.getPropertyPath()).thenReturn(path);
        when(v2.getMessage()).thenReturn("must not be null");

        StepVerifier.create(globalHandlerError.renderError(postRequest(),
                        new ConstraintViolationException(Set.of(v1, v2))))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldRenderWebExchangeBindException() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        FieldError fieldError = new FieldError("obj", "name", "must not be blank");
        when(ex.getFieldErrors()).thenReturn(List.of(fieldError));

        StepVerifier.create(globalHandlerError.renderError(postRequest(), ex))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldRenderWebExchangeBindExceptionWithNullDefaultMessage() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        FieldError fieldError = new FieldError("obj", "name", null, false, null, null, null);
        when(ex.getFieldErrors()).thenReturn(List.of(fieldError));

        StepVerifier.create(globalHandlerError.renderError(postRequest(), ex))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldHandleNullMessageInIllegalArgumentException() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(), new IllegalArgumentException((String) null)))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    @Test
    void shouldHandleResponseStatusExceptionWithNullReason() {
        StepVerifier.create(globalHandlerError.renderError(postRequest(),
                        new ResponseStatusException(HttpStatus.NOT_FOUND, null)))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(404))
                .verifyComplete();
    }

    @Test
    void shouldReturnOriginalExceptionWhenCycleDetected() {
        RuntimeException root = new RuntimeException("root");
        RuntimeException wrapper = new RuntimeException("wrapper", root);
        root.initCause(wrapper);

        StepVerifier.create(globalHandlerError.renderError(postRequest(), wrapper))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(500))
                .verifyComplete();
    }

    @Test
    void shouldMergeDuplicateBindingErrors() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        FieldError e1 = new FieldError("obj", "name", "must not be blank");
        FieldError e2 = new FieldError("obj", "name", "must not be null");
        when(ex.getFieldErrors()).thenReturn(List.of(e1, e2));

        StepVerifier.create(globalHandlerError.renderError(postRequest(), ex))
                .assertNext(response -> assertThat(response.statusCode().value()).isEqualTo(400))
                .verifyComplete();
    }

    private ServerRequest postRequest() {
        return MockServerRequest.builder()
                .method(HttpMethod.POST)
                .uri(URI.create("/api/v1/franchise"))
                .build();
    }
}