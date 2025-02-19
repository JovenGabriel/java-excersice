package com.threeit.users.annotations;

import com.threeit.users.utils.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    /**
     * Specifies the default error message to be used if the password validation fails.
     *
     * @return the default error message when the password is invalid
     */
    String message() default "Invalid password";

    /**
     * Specifies the validation groups with which the constraint declaration is associated.
     *
     * @return an array of validation group classes
     */
    Class<?>[] groups() default {};

    /**
     * Specifies the payload for clients of a validation constraint. This can be used to
     * carry metadata information or additional details about the validation constraint.
     *
     * @return an array of classes extending Payload, representing the metadata for the constraint
     */
    Class<? extends Payload>[] payload() default {};
}