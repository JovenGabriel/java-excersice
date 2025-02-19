package com.threeit.users.utils;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordValidatorTest {

    /**
     * PasswordValidator is responsible for validating passwords based on a regex pattern.
     * The isValid method checks if a given password string matches a predefined regex pattern.
     * It updates the constraint violation message if the password is invalid.
     */
    private final PasswordValidator passwordValidator = new PasswordValidator();


    @Test
    @DisplayName("Validating null value")
    public void testIsValidWithNullValue() {
        // Arrange
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString()))
                .thenReturn(Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        // Act
        boolean result = passwordValidator.isValid(null, context);

        // Assert
        assertFalse(result);
    }


    @Test
    @DisplayName("Validating empty password")
    public void testIsValidWithEmptyValue() {
        // Arrange
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        ReflectionTestUtils.setField(passwordValidator, "pattern", ".*");

        // Act
        boolean result = passwordValidator.isValid("", context);

        // Assert
        assertTrue(result);
    }


    @Test
    @DisplayName("Validating password with matching pattern")
    public void testIsValidWithMatchingPattern() {
        // Arrange
        String validPassword = "Valid123!";
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        ReflectionTestUtils.setField(passwordValidator, "pattern", "\\w+\\d+!");
        ReflectionTestUtils.setField(passwordValidator, "message", "Invalid password format");

        // Act
        boolean result = passwordValidator.isValid(validPassword, context);

        // Assert
        assertTrue(result);
    }


    @Test
    @DisplayName("Validating password with non-matching pattern")
    public void testIsValidWithNonMatchingPattern() {
        // Arrange
        String invalidPassword = "InvalidPassword";
        ConstraintValidatorContext context = Mockito.mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builderMock = Mockito.mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        Mockito.when(context.buildConstraintViolationWithTemplate(Mockito.anyString())).thenReturn(builderMock);

        ReflectionTestUtils.setField(passwordValidator, "pattern", "\\w+\\d+!");
        ReflectionTestUtils.setField(passwordValidator, "message", "Invalid password format");

        // Act
        boolean result = passwordValidator.isValid(invalidPassword, context);

        // Assert
        assertFalse(result);
        Mockito.verify(context).disableDefaultConstraintViolation();
        Mockito.verify(context).buildConstraintViolationWithTemplate("Invalid password format");
    }
}
