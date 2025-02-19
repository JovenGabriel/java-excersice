package com.threeit.users.utils;

import com.threeit.users.annotations.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    @Value("${validation.password.pattern.regexp}")
    private String pattern;

    @Value("${validation.password.message}")
    private String message;

    /**
     * Validates if the given string value matches a predefined pattern.
     * Updates the constraint violation message if the value is invalid.
     *
     * @param value the string value to be validated
     * @param context the context in which the constraint is evaluated
     * @return true if the string value matches the pattern, false otherwise
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        boolean matches = value.matches(pattern);

        if (!matches) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }

        return matches;
    }
}
