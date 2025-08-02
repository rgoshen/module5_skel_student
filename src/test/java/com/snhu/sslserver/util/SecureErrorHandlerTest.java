package com.snhu.sslserver.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;
import com.snhu.sslserver.model.ErrorResponse;

/**
 * Unit tests for SecureErrorHandler class. Tests secure error handling functionality including
 * correlation ID generation, secure logging, information leakage prevention, and proper error
 * response formatting.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@DisplayName("SecureErrorHandler Tests")
class SecureErrorHandlerTest {

  private SecureErrorHandler secureErrorHandler;

  @BeforeEach
  void setUp() {
    secureErrorHandler = new SecureErrorHandler();
  }

  @Test
  @DisplayName("Should handle cryptographic exceptions with secure error response")
  void shouldHandleCryptographicExceptionsWithSecureErrorResponse() {
    // Arrange
    CryptographicException exception =
        new CryptographicException(ErrorCode.ALGORITHM_NOT_SUPPORTED, "SHA-999 not found");

    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleCryptographicException(exception, "application/json");

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());

    ErrorResponse errorResponse = response.getBody();
    assertEquals(500, errorResponse.getStatus());
    assertEquals("Cryptographic operation failed", errorResponse.getMessage());
    assertNotNull(errorResponse.getCorrelationId());
    assertTrue(errorResponse.getCorrelationId().length() == 8); // UUID substring
    assertNotNull(errorResponse.getTimestamp());
  }

  @Test
  @DisplayName("Should not expose sensitive information in cryptographic error responses")
  void shouldNotExposeSensitiveInformationInCryptographicErrorResponses() {
    // Arrange
    CryptographicException exception =
        new CryptographicException(
            ErrorCode.COMPUTATION_FAILED, "Internal crypto key compromised: key123456");

    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleCryptographicException(exception, "application/json");

    // Assert
    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);

    // Should not contain the actual exception message with sensitive information
    assertThat(errorResponse.getMessage(), not(containsString("key123456")));
    assertThat(errorResponse.getMessage(), not(containsString("compromised")));
    assertThat(errorResponse.getMessage(), not(containsString("Internal crypto")));

    // Should contain only generic, safe message
    assertEquals("Cryptographic operation failed", errorResponse.getMessage());
  }

  @Test
  @DisplayName("Should handle validation exceptions with appropriate error response")
  void shouldHandleValidationExceptionsWithAppropriateErrorResponse() {
    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleValidationException(
            "Input contains SQL injection attempt", "application/json");

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());

    ErrorResponse errorResponse = response.getBody();
    assertEquals(400, errorResponse.getStatus());
    assertEquals("Invalid request parameters", errorResponse.getMessage());
    assertNotNull(errorResponse.getCorrelationId());
  }

  @Test
  @DisplayName("Should not expose user input in validation error responses")
  void shouldNotExposeUserInputInValidationErrorResponses() {
    // Arrange
    String maliciousInput = "'; DROP TABLE users; --";

    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleValidationException(maliciousInput, "application/json");

    // Assert
    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse);

    // Should not contain the malicious input
    assertThat(errorResponse.getMessage(), not(containsString("DROP TABLE")));
    assertThat(errorResponse.getMessage(), not(containsString(maliciousInput)));

    // Should contain only generic, safe message
    assertEquals("Invalid request parameters", errorResponse.getMessage());
  }

  @Test
  @DisplayName("Should handle general exceptions with secure error response")
  void shouldHandleGeneralExceptionsWithSecureErrorResponse() {
    // Arrange
    RuntimeException exception = new RuntimeException("Database connection string: user:pass@host");

    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleGeneralException(exception, "application/json");

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());

    ErrorResponse errorResponse = response.getBody();
    assertEquals(500, errorResponse.getStatus());
    assertEquals("An error occurred while processing your request", errorResponse.getMessage());
    assertNotNull(errorResponse.getCorrelationId());

    // Should not expose database connection details
    assertThat(errorResponse.getMessage(), not(containsString("Database connection")));
    assertThat(errorResponse.getMessage(), not(containsString("user:pass@host")));
  }

  @Test
  @DisplayName("Should handle service exceptions with appropriate error response")
  void shouldHandleServiceExceptionsWithAppropriateErrorResponse() {
    // Arrange
    Exception exception = new java.net.ConnectException("Connection refused to internal service");

    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleServiceException(exception, "application/json");

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertNotNull(response.getBody());

    ErrorResponse errorResponse = response.getBody();
    assertEquals(503, errorResponse.getStatus());
    assertEquals("Service temporarily unavailable", errorResponse.getMessage());
    assertNotNull(errorResponse.getCorrelationId());
  }

  @Test
  @DisplayName("Should generate unique correlation IDs for each error")
  void shouldGenerateUniqueCorrelationIdsForEachError() {
    // Arrange
    Exception exception1 = new RuntimeException("Error 1");
    Exception exception2 = new RuntimeException("Error 2");

    // Act
    ResponseEntity<ErrorResponse> response1 =
        secureErrorHandler.handleGeneralException(exception1, "application/json");
    ResponseEntity<ErrorResponse> response2 =
        secureErrorHandler.handleGeneralException(exception2, "application/json");

    // Assert
    String correlationId1 = response1.getBody().getCorrelationId();
    String correlationId2 = response2.getBody().getCorrelationId();

    assertNotNull(correlationId1);
    assertNotNull(correlationId2);
    assertThat(correlationId1, not(containsString(correlationId2)));
  }

  @Test
  @DisplayName("Should create secure HTML error response without sensitive information")
  void shouldCreateSecureHtmlErrorResponseWithoutSensitiveInformation() {
    // Arrange
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(400)
            .message("Invalid request parameters")
            .correlationId("abc12345")
            .timestamp(Instant.now())
            .build();

    // Act
    String htmlResponse =
        secureErrorHandler.createSecureHtmlErrorResponse(errorResponse, HttpStatus.BAD_REQUEST);

    // Assert
    assertNotNull(htmlResponse);
    assertThat(htmlResponse, containsString("<!DOCTYPE html>"));
    assertThat(htmlResponse, containsString("<html lang=\"en\">"));
    assertThat(htmlResponse, containsString("400"));
    assertThat(htmlResponse, containsString("Bad Request"));
    assertThat(htmlResponse, containsString("Invalid request parameters"));
    assertThat(htmlResponse, containsString("abc12345"));
    assertThat(htmlResponse, containsString("Back to Hash Generator"));

    // Should escape any HTML characters properly
    assertThat(htmlResponse, not(containsString("<script>")));
  }

  @Test
  @DisplayName("Should escape HTML in error response to prevent XSS")
  void shouldEscapeHtmlInErrorResponseToPreventXss() {
    // Arrange
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(400)
            .message("<script>alert('xss')</script>")
            .correlationId("test123")
            .timestamp(Instant.now())
            .build();

    // Act
    String htmlResponse =
        secureErrorHandler.createSecureHtmlErrorResponse(errorResponse, HttpStatus.BAD_REQUEST);

    // Assert
    assertThat(htmlResponse, containsString("&lt;script&gt;alert(&#x27;xss&#x27;)&lt;/script&gt;"));
    assertThat(htmlResponse, not(containsString("<script>alert('xss')</script>")));
  }

  @Test
  @DisplayName("Should include professional styling in HTML error responses")
  void shouldIncludeProfessionalStylingInHtmlErrorResponses() {
    // Arrange
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(500)
            .message("An error occurred while processing your request")
            .correlationId("xyz789")
            .timestamp(Instant.now())
            .build();

    // Act
    String htmlResponse =
        secureErrorHandler.createSecureHtmlErrorResponse(
            errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);

    // Assert
    assertThat(htmlResponse, containsString("linear-gradient"));
    assertThat(htmlResponse, containsString("Segoe UI"));
    assertThat(htmlResponse, containsString("border-radius"));
    assertThat(htmlResponse, containsString("box-shadow"));
    assertThat(htmlResponse, containsString("error-container"));
    assertThat(htmlResponse, containsString("error-header"));
    assertThat(htmlResponse, containsString("error-content"));
  }

  @Test
  @DisplayName("Should set appropriate timestamp for error responses")
  void shouldSetAppropriateTimestampForErrorResponses() {
    // Arrange
    Instant beforeTest = Instant.now().minus(1, ChronoUnit.SECONDS);
    Exception exception = new RuntimeException("Test error");

    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleGeneralException(exception, "application/json");

    // Assert
    Instant afterTest = Instant.now().plus(1, ChronoUnit.SECONDS);
    ErrorResponse errorResponse = response.getBody();
    assertNotNull(errorResponse.getTimestamp());
    assertTrue(errorResponse.getTimestamp().isAfter(beforeTest));
    assertTrue(errorResponse.getTimestamp().isBefore(afterTest));
  }

  @Test
  @DisplayName("Should handle null accept header gracefully")
  void shouldHandleNullAcceptHeaderGracefully() {
    // Arrange
    Exception exception = new RuntimeException("Test error");

    // Act
    ResponseEntity<ErrorResponse> response =
        secureErrorHandler.handleGeneralException(exception, null);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
  }
}
