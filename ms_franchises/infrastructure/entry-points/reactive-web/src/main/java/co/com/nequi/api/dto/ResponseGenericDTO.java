package co.com.nequi.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseGenericDTO<T>(
        int statusCode,
        String message,
        T data,
        T error
) {
}
