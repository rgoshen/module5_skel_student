package com.snhu.sslserver.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.snhu.sslserver.util.SecureErrorHandler;

/**
 * Global exception handler that provides centralized error handling across the entire application.
 * This handler intercepts exceptions from all controllers and uses the SecureErrorHandler to
 * provide consistent, secure error responses.
 *
 * <p>Security features:
 *
 * <ul>
 *   <li>Centralized error handling prevents information leakage across the application
 *   <li>Consistent error response format for all endpoints
 *   <li>Secure logging with correlation IDs for debugging
 *   <li>Separation of concerns between business logic and error handling
 * </ul>
 *
 * @author Rick Goshen
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private final SecureErrorHandler secureErrorHandler;

  /**
   * Constructs GlobalExceptionHandler with required dependencies.
   *
   * @param secureErrorHandler Secure error handler for processing exceptions
   */
  @Autowired
  public GlobalExceptionHandler(SecureErrorHandler secureErrorHandler) {
    this.secureErrorHandler = secureErrorHandler;
  }

  /**
   * Handles cryptographic exceptions with secure error responses.
   *
   * @param exception The cryptographic exception
   * @param request The web request for extracting Accept header
   * @return Secure error response with correlation ID
   */
  @ExceptionHandler(CryptographicException.class)
  public ResponseEntity<?> handleCryptographicException(
      CryptographicException exception, WebRequest request) {
    String acceptHeader = extractAcceptHeader(request);
    return secureErrorHandler.handleCryptographicException(exception, acceptHeader);
  }

  /**
   * Handles validation exceptions (IllegalArgumentException) with appropriate error responses.
   *
   * @param exception The validation exception
   * @param request The web request for extracting Accept header
   * @return Validation error response with correlation ID
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleValidationException(
      IllegalArgumentException exception, WebRequest request) {
    String acceptHeader = extractAcceptHeader(request);
    return secureErrorHandler.handleValidationException(exception.getMessage(), acceptHeader);
  }

  /**
   * Handles service unavailability exceptions.
   *
   * @param exception The service exception
   * @param request The web request for extracting Accept header
   * @return Service error response with correlation ID
   */
  @ExceptionHandler({
    java.net.ConnectException.class,
    java.util.concurrent.TimeoutException.class,
    org.springframework.dao.DataAccessException.class
  })
  public ResponseEntity<?> handleServiceException(Exception exception, WebRequest request) {
    String acceptHeader = extractAcceptHeader(request);
    return secureErrorHandler.handleServiceException(exception, acceptHeader);
  }

  /**
   * Handles all other unexpected exceptions with secure error responses.
   *
   * @param exception The general exception
   * @param request The web request for extracting Accept header
   * @return Generic error response with correlation ID
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneralException(Exception exception, WebRequest request) {
    String acceptHeader = extractAcceptHeader(request);
    return secureErrorHandler.handleGeneralException(exception, acceptHeader);
  }

  /**
   * Extracts the Accept header from the web request.
   *
   * @param request The web request
   * @return Accept header value or default to text/html
   */
  private String extractAcceptHeader(WebRequest request) {
    String acceptHeader = request.getHeader("Accept");
    return acceptHeader != null ? acceptHeader : "text/html";
  }
}
