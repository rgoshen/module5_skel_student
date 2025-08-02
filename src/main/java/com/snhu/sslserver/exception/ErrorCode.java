package com.snhu.sslserver.exception;

/**
 * Enumeration of error codes for cryptographic operations. These codes provide structured error
 * categorization while maintaining security by not exposing sensitive implementation details.
 *
 * @author Rick Goshen
 * @version 1.0
 */
public enum ErrorCode {

  /** The requested cryptographic algorithm is not supported by the system. */
  ALGORITHM_NOT_SUPPORTED("The specified algorithm is not supported"),

  /** The requested algorithm is deprecated or cryptographically insecure. */
  ALGORITHM_INSECURE("The specified algorithm is not secure and cannot be used"),

  /** Hash computation failed due to internal cryptographic error. */
  COMPUTATION_FAILED("Hash computation failed"),

  /** Input validation failed due to invalid or malicious data. */
  INPUT_VALIDATION_FAILED("Input validation failed"),

  /** System configuration error preventing cryptographic operations. */
  CONFIGURATION_ERROR("System configuration error");

  private final String defaultMessage;

  /**
   * Constructs an ErrorCode with a default user-safe message.
   *
   * @param defaultMessage The default message for this error code
   */
  ErrorCode(String defaultMessage) {
    this.defaultMessage = defaultMessage;
  }

  /**
   * Gets the default user-safe message for this error code.
   *
   * @return The default message string
   */
  public String getDefaultMessage() {
    return defaultMessage;
  }
}
