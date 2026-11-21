package co.com.nequi.api.utils;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


import java.util.Set;

@Component
@RequiredArgsConstructor
public class ObjectValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T object) {
        if (object == null) {
            return Mono.error(new IllegalArgumentException("The body is required"));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            return Mono.error(new ConstraintViolationException(violations));
        }

        return Mono.just(object);
    }
}
