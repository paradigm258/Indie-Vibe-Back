package com.swp493.ivb.util;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * CustomValidation
 */
public class CustomValidation {

    public static <T> Optional<String> validate(T object) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            return Optional.of(violations.stream().map(failure -> failure.getMessage())
                    .collect(Collectors.toList()).get(0));
        }
        return Optional.empty();
    }
}