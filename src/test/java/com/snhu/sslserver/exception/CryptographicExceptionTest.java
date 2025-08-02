package com.snhu.sslserver.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CryptographicException class. Tests constructors, getters, and exception behavior.
 */
@DisplayName("CryptographicException Tests")
class CryptographicExceptionTest {

  private static final ErrorCode SAMPLE_ERROR_CODE = ErrorCode.ALGORITHM_NOT_SUPPORTED;
  private static final String SAMPLE_USER_MESSAGE = "The specified algorithm is not supported";
  private static final String SAMPLE_CAUSE_MESSAGE = "NoSuchAlgorithmException: MD5";

  @Test
  @DisplayName("Should create exception with error code and user message")
  void shouldCreateExceptionWithErrorCodeAndUserMessage() {
    // When
    CryptographicException exception =
        new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE);

    // Then
    assertEquals(SAMPLE_ERROR_CODE, exception.getErrorCode());
    assertEquals(SAMPLE_USER_MESSAGE, exception.getUserMessage());
    assertEquals(SAMPLE_USER_MESSAGE, exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  @DisplayName("Should create exception with error code, user message, and cause")
  void shouldCreateExceptionWithErrorCodeUserMessageAndCause() {
    // Given
    RuntimeException cause = new RuntimeException(SAMPLE_CAUSE_MESSAGE);

    // When
    CryptographicException exception =
        new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE, cause);

    // Then
    assertEquals(SAMPLE_ERROR_CODE, exception.getErrorCode());
    assertEquals(SAMPLE_USER_MESSAGE, exception.getUserMessage());
    assertEquals(SAMPLE_USER_MESSAGE, exception.getMessage());
    assertEquals(cause, exception.getCause());
    assertEquals(SAMPLE_CAUSE_MESSAGE, exception.getCause().getMessage());
  }

  @Test
  @DisplayName("Should handle all error codes")
  void shouldHandleAllErrorCodes() {
    // Test each error code
    for (ErrorCode errorCode : ErrorCode.values()) {
      // When
      CryptographicException exception = new CryptographicException(errorCode, "Test message");

      // Then
      assertEquals(errorCode, exception.getErrorCode());
      assertEquals("Test message", exception.getUserMessage());
    }
  }

  @Test
  @DisplayName("Should be throwable and catchable")
  void shouldBeThrowableAndCatchable() {
    // When & Then
    assertThrows(
        CryptographicException.class,
        () -> {
          throw new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE);
        });
  }

  @Test
  @DisplayName("Should be catchable as Exception")
  void shouldBeCatchableAsException() {
    // When & Then
    assertThrows(
        Exception.class,
        () -> {
          throw new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE);
        });
  }

  @Test
  @DisplayName("Should preserve stack trace")
  void shouldPreserveStackTrace() {
    // When
    CryptographicException exception =
        new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE);

    // Then
    assertNotNull(exception.getStackTrace());
    assertTrue(exception.getStackTrace().length > 0);
    assertEquals("shouldPreserveStackTrace", exception.getStackTrace()[0].getMethodName());
  }

  @Test
  @DisplayName("Should preserve cause stack trace")
  void shouldPreserveCauseStackTrace() {
    // Given
    RuntimeException cause = new RuntimeException(SAMPLE_CAUSE_MESSAGE);

    // When
    CryptographicException exception =
        new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE, cause);

    // Then
    assertNotNull(exception.getCause().getStackTrace());
    assertTrue(exception.getCause().getStackTrace().length > 0);
  }

  @Test
  @DisplayName("Should handle null error code gracefully")
  void shouldHandleNullErrorCodeGracefully() {
    // When
    CryptographicException exception = new CryptographicException(null, SAMPLE_USER_MESSAGE);

    // Then
    assertNull(exception.getErrorCode());
    assertEquals(SAMPLE_USER_MESSAGE, exception.getUserMessage());
  }

  @Test
  @DisplayName("Should handle null user message gracefully")
  void shouldHandleNullUserMessageGracefully() {
    // When
    CryptographicException exception = new CryptographicException(SAMPLE_ERROR_CODE, null);

    // Then
    assertEquals(SAMPLE_ERROR_CODE, exception.getErrorCode());
    assertNull(exception.getUserMessage());
    assertNull(exception.getMessage());
  }

  @Test
  @DisplayName("Should handle null cause gracefully")
  void shouldHandleNullCauseGracefully() {
    // When
    CryptographicException exception =
        new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE, null);

    // Then
    assertEquals(SAMPLE_ERROR_CODE, exception.getErrorCode());
    assertEquals(SAMPLE_USER_MESSAGE, exception.getUserMessage());
    assertNull(exception.getCause());
  }

  @Test
  @DisplayName("Should support exception chaining")
  void shouldSupportExceptionChaining() {
    // Given
    RuntimeException rootCause = new RuntimeException("Root cause");
    IllegalArgumentException intermediateCause =
        new IllegalArgumentException("Intermediate", rootCause);

    // When
    CryptographicException exception =
        new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE, intermediateCause);

    // Then
    assertEquals(intermediateCause, exception.getCause());
    assertEquals(rootCause, exception.getCause().getCause());
  }

  @Test
  @DisplayName("Should have proper toString representation")
  void shouldHaveProperToStringRepresentation() {
    // When
    CryptographicException exception =
        new CryptographicException(SAMPLE_ERROR_CODE, SAMPLE_USER_MESSAGE);
    String toString = exception.toString();

    // Then
    assertTrue(toString.contains("CryptographicException"));
    assertTrue(toString.contains(SAMPLE_USER_MESSAGE));
  }

  @Test
  @DisplayName("Should work with try-with-resources pattern")
  void shouldWorkWithTryWithResourcesPattern() {
    // This test ensures the exception can be used in typical cryptographic scenarios
    assertThrows(
        CryptographicException.class,
        () -> {
          try {
            // Simulate a cryptographic operation that fails
            throw new RuntimeException("Crypto operation failed");
          } catch (RuntimeException e) {
            throw new CryptographicException(
                ErrorCode.COMPUTATION_FAILED, "Hash computation failed", e);
          }
        });
  }

  @Test
  @DisplayName("Should maintain error code consistency")
  void shouldMaintainErrorCodeConsistency() {
    // Given
    CryptographicException exception1 =
        new CryptographicException(ErrorCode.ALGORITHM_INSECURE, "Message 1");
    CryptographicException exception2 =
        new CryptographicException(ErrorCode.ALGORITHM_INSECURE, "Message 2");

    // Then
    assertEquals(exception1.getErrorCode(), exception2.getErrorCode());
    assertNotEquals(exception1.getUserMessage(), exception2.getUserMessage());
  }
}
