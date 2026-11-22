package co.com.nequi.api.utils;

import co.com.nequi.api.dto.ResponseGenericDTO;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@UtilityClass
public class ResponseUtils {

    public static <T> Mono<ServerResponse> buildCreatedResponse(T data, String message) {
        ResponseGenericDTO<T> body = new ResponseGenericDTO<>(
                HttpStatus.CREATED.value(),
                message,
                data,
                null
        );
        return ServerResponse.status(HttpStatus.CREATED).bodyValue(body);
    }

    public static <T> Mono<ServerResponse> buildDeletedResponse(String message) {
        ResponseGenericDTO<T> body = new ResponseGenericDTO<>(
                HttpStatus.NO_CONTENT.value(),
                message,
                null,
                null
        );
        return ServerResponse.status(HttpStatus.NO_CONTENT).bodyValue(body);
    }

    public static <T> Mono<ServerResponse> buildOkResponse(T data, String message) {
        ResponseGenericDTO<T> body = new ResponseGenericDTO<>(
                HttpStatus.OK.value(),
                message,
                data,
                null
        );
        return ServerResponse.ok().bodyValue(body);
    }
}