package co.com.nequi.api.utils;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class ObjectValidatorTest {

    private ObjectValidator objectValidator;

    @BeforeEach
    void setUp() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        objectValidator = new ObjectValidator(validator);
    }

    @Test
    void shouldReturnObjectWhenValid() {
        DummyObject valid = new DummyObject("value");

        StepVerifier.create(objectValidator.validate(valid))
                .expectNext(valid)
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenInvalid() {
        DummyObject invalid = new DummyObject("");

        StepVerifier.create(objectValidator.validate(invalid))
                .expectError(ConstraintViolationException.class)
                .verify();
    }

    @Test
    void shouldReturnErrorWhenNull() {
        StepVerifier.create(objectValidator.validate(null))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    record DummyObject(@NotBlank String name) {
    }
}