package com.snhu.sslserver.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable data model representing a secure error response. This class encapsulates error
 * information in a standardized format while preventing sensitive information leakage.
 *
 * <p>Security features:
 *
 * <ul>
 *   <li>Sanitized error messages that don't expose internal system details
 *   <li>Correlation IDs for request tracing without revealing sensitive data
 *   <li>Structured format for consistent error handling across the application
 *   <li>Timestamp for audit and debugging purposes
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
public class ErrorResponse {

  private final int status;
  private final String message;
  private final String correlationId;
  private final Instant timestamp;

  /**
   * Constructs an ErrorResponse with all required fields.
   *
   * @param status HTTP status code
   * @param message User-safe error message
   * @param correlationId Unique correlation ID for request tracing
   * @param timestamp When the error occurred
   * @throws IllegalArgumentException if any required field is null or invalid
   */
  public ErrorResponse(int status, String message, String correlationId, Instant timestamp) {
    if (status < 100 || status > 599) {
      throw new IllegalArgumentException("Status code must be between 100-599: " + status);
    }
    if (message == null || message.trim().isEmpty()) {
      throw new IllegalArgumentException("Message cannot be null or empty");
    }
    if (correlationId == null || correlationId.trim().isEmpty()) {
      throw new IllegalArgumentException("Correlation ID cannot be null or empty");
    }
    if (timestamp == null) {
      throw new IllegalArgumentException("Timestamp cannot be null");
    }

    this.status = status;
    this.message = message.trim();
    this.correlationId = correlationId.trim();
    this.timestamp = timestamp;
  }

  /**
   * Gets the HTTP status code.
   *
   * @return HTTP status code (100-599)
   */
  public int getStatus() {
    return status;
  }

  /**
   * Gets the user-safe error message.
   *
   * @return Error message suitable for display to users
   */
  public String getMessage() {
    return message;
  }

  /**
   * Gets the correlation ID for request tracing.
   *
   * @return Unique correlation ID
   */
  public String getCorrelationId() {
    return correlationId;
  }

  /**
   * Gets the timestamp when the error occurred.
   *
   * @return Error timestamp
   */
  public Instant getTimestamp() {
    return timestamp;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    ErrorResponse that = (ErrorResponse) obj;
    return status == that.status
        && Objects.equals(message, that.message)
        && Objects.equals(correlationId, that.correlationId)
        && Objects.equals(timestamp, that.timestamp);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, message, correlationId, timestamp);
  }

  @Override
  public String toString() {
    return "ErrorResponse{"
        + "status="
        + status
        + ", message='"
        + message
        + '\''
        + ", correlationId='"
        + correlationId
        + '\''
        + ", timestamp="
        + timestamp
        + '}';
  }

  /**
   * Builder class for constructing ErrorResponse instances. Provides a fluent interface for
   * creating ErrorResponse objects with default timestamp.
   */
  public static class Builder {
    private int status;
    private String message;
    private String correlationId;
    private Instant timestamp = Instant.now();

    public Builder status(int status) {
      this.status = status;
      return this;
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder correlationId(String correlationId) {
      this.correlationId = correlationId;
      return this;
    }

    public Builder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public ErrorResponse build() {
      return new ErrorResponse(status, message, correlationId, timestamp);
    }
  }

  /**
   * Creates a new Builder instance for constructing ErrorResponse objects.
   *
   * @return A new Builder instance
   */
  public static Builder builder() {
    return new Builder();
  }
}
