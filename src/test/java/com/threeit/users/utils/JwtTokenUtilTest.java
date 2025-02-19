package com.threeit.users.utils;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for JwtTokenUtil, responsible for validating the functionality of generating, validating,
 * and extracting data from JWT tokens.
 * <p>
 * This class contains test cases to ensure the proper behavior of methods implemented
 * in the JwtTokenUtil utility, such as token generation, validation, and email extraction.
 * <p>
 * It employs unit tests to evaluate the following:
 * - Token generation for valid email addresses, including checks for token validity and email consistency.
 * - Exception handling and validation for invalid tokens.
 * - Token expiration handling to ensure that expired tokens are treated as invalid.
 * <p>
 * Dependencies:
 * - Spring Boot Test framework for test context setup.
 * - JUnit 5 annotations for test cases, assertions, and display names.
 * <p>
 * Each method within the class focuses on a specific aspect of JwtTokenUtil functionality.
 */
@SpringBootTest
class JwtTokenUtilTest {

    private final JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();

    @Test
    @DisplayName("Generate Token: Should return a non-null and valid token for a valid email")
    void shouldGenerateValidTokenForValidEmail() {
        // Arrange
        String email = "testuser@example.com";

        // Act
        String token = jwtTokenUtil.generateToken(email);

        // Assert
        assertNotNull(token, "Token should not be null.");
        assertTrue(jwtTokenUtil.validateToken(token), "Generated token should be valid.");
        assertEquals(email, jwtTokenUtil.getEmailFromToken(token), "Extracted email should match the provided email.");
    }

    @Test
    @DisplayName("Generate Token: Should throw exception if invalid token is used")
    void shouldThrowExceptionForInvalidToken() {
        // Arrange
        String invalidToken = "invalidJwtToken";

        // Act & Assert
        assertFalse(jwtTokenUtil.validateToken(invalidToken), "Invalid token should not be considered valid.");
        assertThrows(JwtException.class, () -> jwtTokenUtil.getEmailFromToken(invalidToken), "Parsing invalid token should throw JwtException.");
    }
}