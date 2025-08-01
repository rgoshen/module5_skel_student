package com.snhu.sslserver.util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.SslConfigurationException;
import com.snhu.sslserver.model.ErrorResponse;

/**
 * Centralized secure error handler that provides consistent error processing, logging, and response
 * generation while preventing sensitive information leakage.
 *
 * <p>Security features:
 *
 * <ul>
 *   <li>Correlation IDs for request tracing without exposing sensitive data
 *   <li>Sanitized error messages that don't reveal internal system details
 *   <li>Structured logging with security-aware message filtering
 *   <li>Consistent HTTP status code mapping
 *   <li>Content negotiation support for error responses
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class SecureErrorHandler {

  private static final Logger logger = LoggerFactory.getLogger(SecureErrorHandler.class);

  private static final String CORRELATION_ID_KEY = "correlationId";
  private static final String GENERIC_ERROR_MESSAGE =
      "An error occurred while processing your request";
  private static final String GENERIC_VALIDATION_ERROR = "Invalid request parameters";
  private static final String GENERIC_CRYPTO_ERROR = "Cryptographic operation failed";
  private static final String GENERIC_SERVICE_ERROR = "Service temporarily unavailable";
  private static final String GENERIC_SSL_ERROR = "SSL configuration error";

  /**
   * Handles cryptographic exceptions with secure logging and appropriate error responses.
   *
   * @param exception The cryptographic exception to handle
   * @param acceptHeader Accept header for content negotiation
   * @return Secure error response with correlation ID
   */
  public ResponseEntity<?> handleCryptographicException(
      CryptographicException exception, String acceptHeader) {
    String correlationId = generateCorrelationId();

    // Log with correlation ID and sanitized message
    logSecurely(
        "Cryptographic operation failed",
        correlationId,
        exception,
        "errorCode",
        exception.getErrorCode().name());

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message(GENERIC_CRYPTO_ERROR)
            .correlationId(correlationId)
            .build();

    return createErrorResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR, acceptHeader);
  }

  /**
   * Handles SSL configuration exceptions with secure logging and appropriate error responses.
   *
   * @param exception The SSL configuration exception to handle
   * @param acceptHeader Accept header for content negotiation
   * @return Secure SSL error response with correlation ID
   */
  public ResponseEntity<?> handleSslException(
      SslConfigurationException exception, String acceptHeader) {
    String correlationId = generateCorrelationId();

    // Log with correlation ID and sanitized message - don't expose SSL details
    logSecurely(
        "SSL configuration error occurred",
        correlationId,
        exception,
        "errorType",
        exception.getErrorType().name());

    // Use user-safe message from exception
    String userMessage = exception.getUserMessage();
    if (userMessage == null || userMessage.trim().isEmpty()) {
      userMessage = GENERIC_SSL_ERROR;
    }

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .message(userMessage)
            .correlationId(correlationId)
            .build();

    return createErrorResponseEntity(errorResponse, HttpStatus.SERVICE_UNAVAILABLE, acceptHeader);
  }

  /**
   * Handles validation exceptions with appropriate error responses.
   *
   * @param message Validation error message
   * @param acceptHeader Accept header for content negotiation
   * @return Validation error response with correlation ID
   */
  public ResponseEntity<?> handleValidationException(String message, String acceptHeader) {
    String correlationId = generateCorrelationId();

    // Log validation error without exposing user input
    logSecurely("Validation failed", correlationId, null, "type", "validation");

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .message(GENERIC_VALIDATION_ERROR)
            .correlationId(correlationId)
            .build();

    return createErrorResponseEntity(errorResponse, HttpStatus.BAD_REQUEST, acceptHeader);
  }

  /**
   * Handles general service exceptions with secure error responses.
   *
   * @param exception The general exception to handle
   * @param acceptHeader Accept header for content negotiation
   * @return Generic error response with correlation ID
   */
  public ResponseEntity<?> handleGeneralException(Exception exception, String acceptHeader) {
    String correlationId = generateCorrelationId();

    // Log with correlation ID but don't expose exception details
    logSecurely(
        "Unexpected service error",
        correlationId,
        exception,
        "exceptionType",
        exception.getClass().getSimpleName());

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message(GENERIC_ERROR_MESSAGE)
            .correlationId(correlationId)
            .build();

    return createErrorResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR, acceptHeader);
  }

  /**
   * Handles service unavailability with appropriate error responses.
   *
   * @param exception The service exception to handle
   * @param acceptHeader Accept header for content negotiation
   * @return Service error response with correlation ID
   */
  public ResponseEntity<?> handleServiceException(Exception exception, String acceptHeader) {
    String correlationId = generateCorrelationId();

    logSecurely("Service temporarily unavailable", correlationId, exception);

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .status(HttpStatus.SERVICE_UNAVAILABLE.value())
            .message(GENERIC_SERVICE_ERROR)
            .correlationId(correlationId)
            .build();

    return createErrorResponseEntity(errorResponse, HttpStatus.SERVICE_UNAVAILABLE, acceptHeader);
  }

  /**
   * Creates a secure HTML error response for browser requests.
   *
   * @param errorResponse Error response data
   * @param status HTTP status code
   * @return HTML formatted error response
   */
  public String createSecureHtmlErrorResponse(ErrorResponse errorResponse, HttpStatus status) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html>\n");
    html.append("<html lang=\"en\">\n");
    html.append("<head>\n");
    html.append("    <meta charset=\"UTF-8\">\n");
    html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
    html.append("    <title>Error - Checksum Verification System</title>\n");
    html.append("    <style>\n");
    html.append(
        "        body { font-family: 'Segoe UI', Arial, sans-serif; margin: 0; padding: 20px; ");
    html.append(
        "background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; }\n");
    html.append(
        "        .error-container { max-width: 600px; margin: 50px auto; background: white; ");
    html.append(
        "border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); overflow: hidden; }\n");
    html.append(
        "        .error-header { background: #f56565; color: white; padding: 2rem; text-align: center; }\n");
    html.append("        .error-content { padding: 2rem; }\n");
    html.append(
        "        .error-code { font-size: 3rem; font-weight: bold; margin-bottom: 0.5rem; }\n");
    html.append("        .error-message { font-size: 1.2rem; margin-bottom: 1rem; }\n");
    html.append(
        "        .correlation-id { font-size: 0.9rem; color: #666; font-family: monospace; }\n");
    html.append(
        "        .back-link { display: inline-block; margin-top: 1rem; color: #667eea; text-decoration: none; }\n");
    html.append("    </style>\n");
    html.append("</head>\n");
    html.append("<body>\n");
    html.append("    <div class=\"error-container\">\n");
    html.append("        <header class=\"error-header\">\n");
    html.append("            <div class=\"error-code\">").append(status.value()).append("</div>\n");
    html.append("            <div class=\"error-message\">")
        .append(status.getReasonPhrase())
        .append("</div>\n");
    html.append("        </header>\n");
    html.append("        <div class=\"error-content\">\n");
    html.append("            <p>").append(escapeHtml(errorResponse.getMessage())).append("</p>\n");
    html.append("            <p class=\"correlation-id\">Reference ID: ")
        .append(errorResponse.getCorrelationId())
        .append("</p>\n");
    html.append(
        "            <a href=\"/api/v1/hash\" class=\"back-link\">&larr; Back to Hash Generator</a>\n");
    html.append("        </div>\n");
    html.append("    </div>\n");
    html.append("</body>\n");
    html.append("</html>");

    return html.toString();
  }

  /**
   * Generates a unique correlation ID for request tracing.
   *
   * @return Unique correlation ID
   */
  private String generateCorrelationId() {
    // Use a longer substring of the UUID to reduce collision risk
    return UUID.randomUUID().toString().replace("-", "").substring(0, 20);
  }

  /**
   * Performs secure logging with correlation ID and structured data.
   *
   * @param message Log message
   * @param correlationId Correlation ID for request tracing
   * @param exception Optional exception to log
   * @param logDataPairs Additional structured data pairs
   */
  private void logSecurely(
      String message, String correlationId, Exception exception, String... logDataPairs) {
    try {
      // Set correlation ID in MDC for structured logging
      MDC.put(CORRELATION_ID_KEY, correlationId);

      // Add additional structured data
      for (int i = 0; i < logDataPairs.length; i += 2) {
        if (i + 1 < logDataPairs.length) {
          MDC.put(logDataPairs[i], logDataPairs[i + 1]);
        }
      }

      if (exception != null) {
        logger.error("{} [correlationId={}]", message, correlationId, exception);
      } else {
        logger.warn("{} [correlationId={}]", message, correlationId);
      }
    } finally {
      // Always clear MDC to prevent memory leaks
      MDC.clear();
    }
  }

  /**
   * Creates a ResponseEntity with proper error response format based on content negotiation.
   *
   * @param errorResponse Error response data
   * @param status HTTP status code
   * @param acceptHeader Accept header for content negotiation
   * @return ResponseEntity with proper content type
   */
  private ResponseEntity<?> createErrorResponseEntity(
      ErrorResponse errorResponse, HttpStatus status, String acceptHeader) {

    // Add correlation ID to response header for client log correlation
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Correlation-ID", errorResponse.getCorrelationId());

    // Determine response format based on Accept header
    if (acceptHeader != null && acceptHeader.contains("text/html")) {
      String htmlResponse = createSecureHtmlErrorResponse(errorResponse, status);
      headers.setContentType(MediaType.TEXT_HTML);
      return ResponseEntity.status(status).headers(headers).body(htmlResponse);
    } else {
      headers.setContentType(MediaType.APPLICATION_JSON);
      return ResponseEntity.status(status).headers(headers).body(errorResponse);
    }
  }

  /**
   * Escapes HTML special characters to prevent XSS attacks in error messages.
   *
   * @param text Text to escape
   * @return HTML-escaped text
   */
  private String escapeHtml(String text) {
    if (text == null) {
      return "";
    }
    return text.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
        .replace("`", "&#x60;");
  }
}
