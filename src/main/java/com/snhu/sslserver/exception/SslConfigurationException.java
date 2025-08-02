package com.snhu.sslserver.exception;

/**
 * Exception thrown when SSL/TLS configuration issues are detected. This exception is used to handle
 * SSL-specific errors such as certificate store loading failures, certificate validation issues, or
 * TLS protocol configuration problems.
 *
 * <p>This exception supports the secure error handling requirements for the CS305 Module Five
 * checksum verification system by providing structured error information for SSL-related issues.
 *
 * @author Rick Goshen
 * @version 1.0
 */
public class SslConfigurationException extends RuntimeException {

  /** SSL error types for categorizing different SSL configuration issues. */
  public enum SslErrorType {
    CERTSTORE_NOT_FOUND("SSL certificate store file not found"),
    CERTSTORE_LOAD_FAILED("Failed to load SSL certificate store"),
    CERTIFICATE_INVALID("SSL certificate validation failed"),
    PROTOCOL_UNSUPPORTED("SSL/TLS protocol not supported"),
    CIPHER_SUITE_INVALID("Invalid cipher suite configuration"),
    SSL_CONTEXT_CREATION_FAILED("Failed to create SSL context"),
    HTTPS_ENFORCEMENT_FAILED("HTTPS enforcement configuration failed");

    private final String defaultMessage;

    SslErrorType(String defaultMessage) {
      this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
      return defaultMessage;
    }
  }

  private final SslErrorType errorType;

  /**
   * Constructs an SslConfigurationException with specified error type and message.
   *
   * @param errorType The type of SSL error
   * @param message Detailed error message
   */
  public SslConfigurationException(SslErrorType errorType, String message) {
    super(message);
    this.errorType = errorType;
  }

  /**
   * Constructs an SslConfigurationException with specified error type, message, and cause.
   *
   * @param errorType The type of SSL error
   * @param message Detailed error message
   * @param cause The underlying cause of the exception
   */
  public SslConfigurationException(SslErrorType errorType, String message, Throwable cause) {
    super(message, cause);
    this.errorType = errorType;
  }

  /**
   * Constructs an SslConfigurationException with error type using default message.
   *
   * @param errorType The type of SSL error
   */
  public SslConfigurationException(SslErrorType errorType) {
    super(errorType.getDefaultMessage());
    this.errorType = errorType;
  }

  /**
   * Gets the SSL error type.
   *
   * @return The SSL error type
   */
  public SslErrorType getErrorType() {
    return errorType;
  }

  /**
   * Gets a user-safe error message that doesn't expose sensitive SSL configuration details.
   *
   * @return User-safe error message
   */
  public String getUserMessage() {
    switch (errorType) {
      case CERTSTORE_NOT_FOUND:
      case CERTSTORE_LOAD_FAILED:
        return "SSL configuration error - please check system settings";
      case CERTIFICATE_INVALID:
        return "SSL certificate validation failed";
      case PROTOCOL_UNSUPPORTED:
      case CIPHER_SUITE_INVALID:
        return "SSL protocol configuration error";
      case SSL_CONTEXT_CREATION_FAILED:
      case HTTPS_ENFORCEMENT_FAILED:
        return "SSL service temporarily unavailable";
      default:
        return "SSL configuration error";
    }
  }

  @Override
  public String toString() {
    return "SslConfigurationException{"
        + "errorType="
        + errorType
        + ", message='"
        + getMessage()
        + "'}";
  }
}
