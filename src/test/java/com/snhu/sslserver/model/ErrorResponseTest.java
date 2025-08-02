package com.snhu.sslserver.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ErrorResponse model class. Tests validation, builder pattern, and security
 * features of error response data model.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@DisplayName("ErrorResponse Model Tests")
class ErrorResponseTest {

  @Test
  @DisplayName("Should create valid ErrorResponse with all required fields")
  void shouldCreateValidErrorResponseWithAllRequiredFields() {
    // Arrange
    int status = 400;
    String message = "Invalid request";
    String correlationId = "abc123";
    Instant timestamp = Instant.now();

    // Act
    ErrorResponse errorResponse = new ErrorResponse(status, message, correlationId, timestamp);

    // Assert
    assertEquals(status, errorResponse.getStatus());
    assertEquals(message, errorResponse.getMessage());
    assertEquals(correlationId, errorResponse.getCorrelationId());
    assertEquals(timestamp, errorResponse.getTimestamp());
  }

  @Test
  @DisplayName("Should validate status code is within valid HTTP range")
  void shouldValidateStatusCodeIsWithinValidHttpRange() {
    // Arrange
    String message = "Error message";
    String correlationId = "test123";
    Instant timestamp = Instant.now();

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(99, message, correlationId, timestamp),
        "Status code must be between 100-599: 99");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(600, message, correlationId, timestamp),
        "Status code must be between 100-599: 600");
  }

  @Test
  @DisplayName("Should validate message is not null or empty")
  void shouldValidateMessageIsNotNullOrEmpty() {
    // Arrange
    int status = 400;
    String correlationId = "test123";
    Instant timestamp = Instant.now();

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(status, null, correlationId, timestamp),
        "Message cannot be null or empty");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(status, "", correlationId, timestamp),
        "Message cannot be null or empty");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(status, "   ", correlationId, timestamp),
        "Message cannot be null or empty");
  }

  @Test
  @DisplayName("Should validate correlation ID is not null or empty")
  void shouldValidateCorrelationIdIsNotNullOrEmpty() {
    // Arrange
    int status = 400;
    String message = "Error message";
    Instant timestamp = Instant.now();

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(status, message, null, timestamp),
        "Correlation ID cannot be null or empty");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(status, message, "", timestamp),
        "Correlation ID cannot be null or empty");

    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(status, message, "   ", timestamp),
        "Correlation ID cannot be null or empty");
  }

  @Test
  @DisplayName("Should validate timestamp is not null")
  void shouldValidateTimestampIsNotNull() {
    // Arrange
    int status = 400;
    String message = "Error message";
    String correlationId = "test123";

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> new ErrorResponse(status, message, correlationId, null),
        "Timestamp cannot be null");
  }

  @Test
  @DisplayName("Should trim whitespace from message and correlation ID")
  void shouldTrimWhitespaceFromMessageAndCorrelationId() {
    // Arrange
    int status = 400;
    String message = "  Error message  ";
    String correlationId = "  test123  ";
    Instant timestamp = Instant.now();

    // Act
    ErrorResponse errorResponse = new ErrorResponse(status, message, correlationId, timestamp);

    // Assert
    assertEquals("Error message", errorResponse.getMessage());
    assertEquals("test123", errorResponse.getCorrelationId());
  }

  @Test
  @DisplayName("Should create ErrorResponse using builder pattern")
  void shouldCreateErrorResponseUsingBuilderPattern() {
    // Arrange
    int status = 500;
    String message = "Internal server error";
    String correlationId = "xyz789";

    // Act
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(status)
            .message(message)
            .correlationId(correlationId)
            .build();

    // Assert
    assertEquals(status, errorResponse.getStatus());
    assertEquals(message, errorResponse.getMessage());
    assertEquals(correlationId, errorResponse.getCorrelationId());
    assertNotNull(errorResponse.getTimestamp());

    // Timestamp should be recent (within last few seconds)
    Instant now = Instant.now();
    assertTrue(errorResponse.getTimestamp().isBefore(now.plus(1, ChronoUnit.SECONDS)));
    assertTrue(errorResponse.getTimestamp().isAfter(now.minus(10, ChronoUnit.SECONDS)));
  }

  @Test
  @DisplayName("Should allow custom timestamp in builder")
  void shouldAllowCustomTimestampInBuilder() {
    // Arrange
    Instant customTimestamp = Instant.parse("2025-01-01T12:00:00Z");

    // Act
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(400)
            .message("Bad request")
            .correlationId("custom123")
            .timestamp(customTimestamp)
            .build();

    // Assert
    assertEquals(customTimestamp, errorResponse.getTimestamp());
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Arrange
    Instant timestamp = Instant.now();
    ErrorResponse errorResponse1 = new ErrorResponse(400, "Error message", "test123", timestamp);
    ErrorResponse errorResponse2 = new ErrorResponse(400, "Error message", "test123", timestamp);
    ErrorResponse errorResponse3 =
        new ErrorResponse(500, "Different message", "different123", timestamp);

    // Act & Assert
    assertEquals(errorResponse1, errorResponse2);
    assertEquals(errorResponse1.hashCode(), errorResponse2.hashCode());

    assertNotEquals(errorResponse1, errorResponse3);
    assertNotEquals(errorResponse1.hashCode(), errorResponse3.hashCode());

    assertNotEquals(errorResponse1, null);
    assertNotEquals(errorResponse1, "not an ErrorResponse");
  }

  @Test
  @DisplayName("Should implement toString correctly")
  void shouldImplementToStringCorrectly() {
    // Arrange
    Instant timestamp = Instant.parse("2025-01-01T12:00:00Z");
    ErrorResponse errorResponse = new ErrorResponse(404, "Not found", "xyz789", timestamp);

    // Act
    String toString = errorResponse.toString();

    // Assert
    assertNotNull(toString);
    assertTrue(toString.contains("ErrorResponse{"));
    assertTrue(toString.contains("status=404"));
    assertTrue(toString.contains("message='Not found'"));
    assertTrue(toString.contains("correlationId='xyz789'"));
    assertTrue(toString.contains("timestamp=2025-01-01T12:00:00Z"));
  }

  @Test
  @DisplayName("Should handle edge case HTTP status codes")
  void shouldHandleEdgeCaseHttpStatusCodes() {
    // Arrange
    String message = "Test message";
    String correlationId = "test123";
    Instant timestamp = Instant.now();

    // Act & Assert - Valid edge cases
    ErrorResponse response100 = new ErrorResponse(100, message, correlationId, timestamp);
    assertEquals(100, response100.getStatus());

    ErrorResponse response599 = new ErrorResponse(599, message, correlationId, timestamp);
    assertEquals(599, response599.getStatus());

    // Common HTTP status codes
    ErrorResponse response200 = new ErrorResponse(200, message, correlationId, timestamp);
    assertEquals(200, response200.getStatus());

    ErrorResponse response404 = new ErrorResponse(404, message, correlationId, timestamp);
    assertEquals(404, response404.getStatus());

    ErrorResponse response500 = new ErrorResponse(500, message, correlationId, timestamp);
    assertEquals(500, response500.getStatus());
  }

  @Test
  @DisplayName("Should handle builder validation errors")
  void shouldHandleBuilderValidationErrors() {
    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () ->
            ErrorResponse.builder()
                .status(99) // Invalid status
                .message("Test message")
                .correlationId("test123")
                .build());

    assertThrows(
        IllegalArgumentException.class,
        () ->
            ErrorResponse.builder()
                .status(400)
                .message("") // Empty message
                .correlationId("test123")
                .build());

    assertThrows(
        IllegalArgumentException.class,
        () ->
            ErrorResponse.builder()
                .status(400)
                .message("Test message")
                .correlationId("") // Empty correlation ID
                .build());
  }
}
