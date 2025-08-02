package com.snhu.sslserver.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import com.snhu.sslserver.model.ErrorResponse;
import com.snhu.sslserver.util.SecureErrorHandler;

/**
 * Unit tests for GlobalExceptionHandler class. Tests centralized exception handling functionality,
 * proper delegation to SecureErrorHandler, and correct Accept header extraction.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

  @Mock private SecureErrorHandler secureErrorHandler;

  @Mock private WebRequest webRequest;

  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    globalExceptionHandler = new GlobalExceptionHandler(secureErrorHandler);
  }

  @Test
  @DisplayName("Should handle cryptographic exceptions with proper delegation")
  void shouldHandleCryptographicExceptionsWithProperDelegation() {
    // Arrange
    CryptographicException exception =
        new CryptographicException(ErrorCode.ALGORITHM_NOT_SUPPORTED, "Test crypto error");
    String acceptHeader = "application/json";
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(500)
            .message("Cryptographic operation failed")
            .correlationId("test123")
            .build();
    ResponseEntity<ErrorResponse> expectedResponse =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

    when(webRequest.getHeader("Accept")).thenReturn(acceptHeader);
    when(secureErrorHandler.handleCryptographicException(exception, acceptHeader))
        .thenReturn(expectedResponse);

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleCryptographicException(exception, webRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(errorResponse, response.getBody());
    verify(secureErrorHandler).handleCryptographicException(exception, acceptHeader);
  }

  @Test
  @DisplayName("Should handle validation exceptions with proper delegation")
  void shouldHandleValidationExceptionsWithProperDelegation() {
    // Arrange
    IllegalArgumentException exception = new IllegalArgumentException("Invalid input data");
    String acceptHeader = "text/html";
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(400)
            .message("Invalid request parameters")
            .correlationId("test456")
            .build();
    ResponseEntity<ErrorResponse> expectedResponse =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

    when(webRequest.getHeader("Accept")).thenReturn(acceptHeader);
    when(secureErrorHandler.handleValidationException("Invalid input data", acceptHeader))
        .thenReturn(expectedResponse);

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleValidationException(exception, webRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(errorResponse, response.getBody());
    verify(secureErrorHandler).handleValidationException("Invalid input data", acceptHeader);
  }

  @Test
  @DisplayName("Should handle service exceptions with proper delegation")
  void shouldHandleServiceExceptionsWithProperDelegation() {
    // Arrange
    java.net.ConnectException exception = new java.net.ConnectException("Connection refused");
    String acceptHeader = "application/json";
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(503)
            .message("Service temporarily unavailable")
            .correlationId("test789")
            .build();
    ResponseEntity<ErrorResponse> expectedResponse =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);

    when(webRequest.getHeader("Accept")).thenReturn(acceptHeader);
    when(secureErrorHandler.handleServiceException(exception, acceptHeader))
        .thenReturn(expectedResponse);

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleServiceException(exception, webRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    assertEquals(errorResponse, response.getBody());
    verify(secureErrorHandler).handleServiceException(exception, acceptHeader);
  }

  @Test
  @DisplayName("Should handle general exceptions with proper delegation")
  void shouldHandleGeneralExceptionsWithProperDelegation() {
    // Arrange
    RuntimeException exception = new RuntimeException("Unexpected error");
    String acceptHeader = "text/html";
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(500)
            .message("An error occurred while processing your request")
            .correlationId("test999")
            .build();
    ResponseEntity<ErrorResponse> expectedResponse =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

    when(webRequest.getHeader("Accept")).thenReturn(acceptHeader);
    when(secureErrorHandler.handleGeneralException(exception, acceptHeader))
        .thenReturn(expectedResponse);

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleGeneralException(exception, webRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(errorResponse, response.getBody());
    verify(secureErrorHandler).handleGeneralException(exception, acceptHeader);
  }

  @Test
  @DisplayName("Should extract Accept header correctly")
  void shouldExtractAcceptHeaderCorrectly() {
    // Arrange
    CryptographicException exception =
        new CryptographicException(ErrorCode.COMPUTATION_FAILED, "Test error");
    String acceptHeader = "application/json, text/html;q=0.9";

    when(webRequest.getHeader("Accept")).thenReturn(acceptHeader);
    when(secureErrorHandler.handleCryptographicException(any(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    // Act
    globalExceptionHandler.handleCryptographicException(exception, webRequest);

    // Assert
    verify(webRequest).getHeader("Accept");
    verify(secureErrorHandler).handleCryptographicException(exception, acceptHeader);
  }

  @Test
  @DisplayName("Should default to text/html when Accept header is null")
  void shouldDefaultToTextHtmlWhenAcceptHeaderIsNull() {
    // Arrange
    RuntimeException exception = new RuntimeException("Test error");

    when(webRequest.getHeader("Accept")).thenReturn(null);
    when(secureErrorHandler.handleGeneralException(any(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    // Act
    globalExceptionHandler.handleGeneralException(exception, webRequest);

    // Assert
    verify(secureErrorHandler).handleGeneralException(exception, "text/html");
  }

  @Test
  @DisplayName("Should handle empty Accept header gracefully")
  void shouldHandleEmptyAcceptHeaderGracefully() {
    // Arrange
    IllegalArgumentException exception = new IllegalArgumentException("Validation failed");

    when(webRequest.getHeader("Accept")).thenReturn("");
    when(secureErrorHandler.handleValidationException(anyString(), anyString()))
        .thenReturn(ResponseEntity.ok().build());

    // Act
    globalExceptionHandler.handleValidationException(exception, webRequest);

    // Assert
    verify(secureErrorHandler).handleValidationException("Validation failed", "");
  }

  @Test
  @DisplayName("Should handle TimeoutException in service exception handler")
  void shouldHandleTimeoutExceptionInServiceExceptionHandler() {
    // Arrange
    java.util.concurrent.TimeoutException exception =
        new java.util.concurrent.TimeoutException("Operation timed out");
    String acceptHeader = "application/json";
    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(503)
            .message("Service temporarily unavailable")
            .correlationId("timeout123")
            .build();
    ResponseEntity<ErrorResponse> expectedResponse =
        ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);

    when(webRequest.getHeader("Accept")).thenReturn(acceptHeader);
    when(secureErrorHandler.handleServiceException(exception, acceptHeader))
        .thenReturn(expectedResponse);

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleServiceException(exception, webRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    verify(secureErrorHandler).handleServiceException(exception, acceptHeader);
  }

  @Test
  @DisplayName("Should handle DataAccessException in service exception handler")
  void shouldHandleDataAccessExceptionInServiceExceptionHandler() {
    // Arrange
    org.springframework.dao.DataAccessException exception =
        mock(org.springframework.dao.DataAccessException.class);
    String acceptHeader = "text/html";

    when(webRequest.getHeader("Accept")).thenReturn(acceptHeader);
    when(secureErrorHandler.handleServiceException(any(), anyString()))
        .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

    // Act
    ResponseEntity<ErrorResponse> response =
        globalExceptionHandler.handleServiceException(exception, webRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    verify(secureErrorHandler).handleServiceException(exception, acceptHeader);
  }
}
