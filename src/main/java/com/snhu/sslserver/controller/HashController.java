package com.snhu.sslserver.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.model.AlgorithmInfo;
import com.snhu.sslserver.model.HashResult;
import com.snhu.sslserver.model.ValidationResult;
import com.snhu.sslserver.service.IHashService;
import com.snhu.sslserver.service.IInputValidator;

/**
 * REST controller for hash generation endpoints. This controller provides secure checksum
 * verification services through RESTful APIs with content negotiation support for both HTML and
 * JSON responses.
 *
 * <p>Security features: - Input validation and sanitization - Algorithm security validation -
 * Secure error handling without information leakage - HTTPS-only operation
 *
 * @author Rick Goshen
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class HashController {

  private final IHashService hashService;
  private final IInputValidator validator;

  /**
   * Constructs HashController with required dependencies.
   *
   * @param hashService Service for hash computation operations
   * @param validator Service for input validation and sanitization
   * @throws IllegalArgumentException if any dependency is null
   */
  public HashController(IHashService hashService, IInputValidator validator) {
    this.hashService = Objects.requireNonNull(hashService, "Hash service cannot be null");
    this.validator = Objects.requireNonNull(validator, "Input validator cannot be null");
  }

  /**
   * Generates cryptographic hash for student name with specified algorithm. Supports content
   * negotiation for HTML and JSON responses.
   *
   * @param algorithm The hash algorithm to use (defaults to SHA-256)
   * @param acceptHeader The Accept header for content negotiation
   * @return ResponseEntity with hash result in requested format
   */
  @GetMapping(
      value = "/hash",
      produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> generateHash(
      @RequestParam(defaultValue = "SHA-256") String algorithm,
      @RequestHeader(value = "Accept", defaultValue = "text/html") String acceptHeader) {

    try {
      // Validate algorithm parameter
      ValidationResult algorithmValidation = validator.validateAlgorithm(algorithm);
      if (!algorithmValidation.isValid()) {
        return createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid algorithm: " + String.join(", ", algorithmValidation.getErrors()),
            acceptHeader);
      }

      // Use sanitized algorithm name
      String sanitizedAlgorithm = algorithmValidation.getSanitizedData();

      // Create input data with student name
      String inputData = "Hello Rick Goshen!";

      // Validate input data
      ValidationResult inputValidation = validator.validateAndSanitize(inputData);
      if (!inputValidation.isValid()) {
        return createErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Invalid input data: " + String.join(", ", inputValidation.getErrors()),
            acceptHeader);
      }

      // Compute hash using validated inputs
      HashResult result =
          hashService.computeHash(inputValidation.getSanitizedData(), sanitizedAlgorithm);

      // Return response based on content negotiation
      if (acceptHeader.contains("application/json")) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(createJsonResponse(result));
      } else {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(createHtmlResponse(result));
      }

    } catch (CryptographicException e) {
      return createErrorResponse(
          HttpStatus.INTERNAL_SERVER_ERROR, e.getUserMessage(), acceptHeader);
    } catch (Exception e) {
      // Log the actual error for debugging but don't expose it
      return createErrorResponse(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "An unexpected error occurred during hash computation",
          acceptHeader);
    }
  }

  /**
   * Gets information about supported algorithms.
   *
   * @param acceptHeader The Accept header for content negotiation
   * @return ResponseEntity with algorithm information in requested format
   */
  @GetMapping(
      value = "/algorithms",
      produces = {MediaType.TEXT_HTML_VALUE, MediaType.APPLICATION_JSON_VALUE})
  public ResponseEntity<?> getSupportedAlgorithms(
      @RequestHeader(value = "Accept", defaultValue = "text/html") String acceptHeader) {

    try {
      List<AlgorithmInfo> algorithms = hashService.getSupportedAlgorithms();

      if (acceptHeader.contains("application/json")) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(algorithms);
      } else {
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(createAlgorithmsHtmlResponse(algorithms));
      }

    } catch (Exception e) {
      return createErrorResponse(
          HttpStatus.INTERNAL_SERVER_ERROR,
          "Unable to retrieve algorithm information",
          acceptHeader);
    }
  }

  /**
   * Creates a JSON response object for hash results.
   *
   * @param result The hash computation result
   * @return JSON response object
   */
  private HashResponseDto createJsonResponse(HashResult result) {
    return new HashResponseDto(
        result.getOriginalData(),
        result.getAlgorithm(),
        result.getHexHash(),
        result.getTimestamp().toString(),
        result.getComputationTimeMs());
  }

  /**
   * Creates an HTML response for hash results.
   *
   * @param result The hash computation result
   * @return HTML formatted response string
   */
  private String createHtmlResponse(HashResult result) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html>\n");
    html.append("<html>\n");
    html.append("<head>\n");
    html.append("    <title>Checksum Verification Result</title>\n");
    html.append("    <style>\n");
    html.append("        body { font-family: Arial, sans-serif; margin: 40px; }\n");
    html.append(
        "        .result-container { background-color: #f5f5f5; padding: 20px; border-radius: 5px; }\n");
    html.append("        .field { margin: 10px 0; }\n");
    html.append("        .label { font-weight: bold; }\n");
    html.append("        .hash { font-family: monospace; word-break: break-all; }\n");
    html.append("    </style>\n");
    html.append("</head>\n");
    html.append("<body>\n");
    html.append("    <h1>Checksum Verification System</h1>\n");
    html.append("    <div class=\"result-container\">\n");
    html.append("        <div class=\"field\">\n");
    html.append("            <span class=\"label\">Original Data:</span> ");
    html.append(escapeHtml(result.getOriginalData()));
    html.append("\n        </div>\n");
    html.append("        <div class=\"field\">\n");
    html.append("            <span class=\"label\">Algorithm:</span> ");
    html.append(escapeHtml(result.getAlgorithm()));
    html.append("\n        </div>\n");
    html.append("        <div class=\"field\">\n");
    html.append("            <span class=\"label\">Hash Value:</span>\n");
    html.append("            <div class=\"hash\">");
    html.append(escapeHtml(result.getHexHash()));
    html.append("</div>\n");
    html.append("        </div>\n");
    html.append("        <div class=\"field\">\n");
    html.append("            <span class=\"label\">Computation Time:</span> ");
    html.append(result.getComputationTimeMs());
    html.append(" ms\n        </div>\n");
    html.append("    </div>\n");
    html.append("</body>\n");
    html.append("</html>");
    return html.toString();
  }

  /**
   * Creates an HTML response for supported algorithms.
   *
   * @param algorithms List of supported algorithms
   * @return HTML formatted response string
   */
  private String createAlgorithmsHtmlResponse(List<AlgorithmInfo> algorithms) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html>\n");
    html.append("<html>\n");
    html.append("<head>\n");
    html.append("    <title>Supported Algorithms</title>\n");
    html.append("    <style>\n");
    html.append("        body { font-family: Arial, sans-serif; margin: 40px; }\n");
    html.append(
        "        .algorithm { background-color: #f5f5f5; padding: 15px; margin: 10px 0; border-radius: 5px; }\n");
    html.append("        .secure { border-left: 4px solid #4CAF50; }\n");
    html.append("        .insecure { border-left: 4px solid #f44336; }\n");
    html.append("        .name { font-weight: bold; font-size: 1.2em; }\n");
    html.append("    </style>\n");
    html.append("</head>\n");
    html.append("<body>\n");
    html.append("    <h1>Supported Hash Algorithms</h1>\n");

    for (AlgorithmInfo algorithm : algorithms) {
      html.append("    <div class=\"algorithm ");
      html.append(algorithm.isSecure() ? "secure" : "insecure");
      html.append("\">\n");
      html.append("        <div class=\"name\">");
      html.append(escapeHtml(algorithm.getName()));
      html.append("</div>\n");
      html.append("        <div>");
      html.append(escapeHtml(algorithm.getDescription()));
      html.append("</div>\n");
      html.append("        <div><strong>Security:</strong> ");
      html.append(algorithm.isSecure() ? "Secure" : "Deprecated");
      html.append("</div>\n");
      html.append("        <div><strong>Performance:</strong> ");
      html.append(algorithm.getPerformance().toString());
      html.append("</div>\n");
      html.append("    </div>\n");
    }

    html.append("</body>\n");
    html.append("</html>");
    return html.toString();
  }

  /**
   * Creates error response based on content negotiation.
   *
   * @param status HTTP status code
   * @param message Error message
   * @param acceptHeader Accept header for content negotiation
   * @return ResponseEntity with error response
   */
  private ResponseEntity<?> createErrorResponse(
      HttpStatus status, String message, String acceptHeader) {
    if (acceptHeader.contains("application/json")) {
      ErrorResponseDto errorResponse = new ErrorResponseDto(status.value(), message);
      return ResponseEntity.status(status)
          .contentType(MediaType.APPLICATION_JSON)
          .body(errorResponse);
    } else {
      String htmlError = createErrorHtmlResponse(status, message);
      return ResponseEntity.status(status).contentType(MediaType.TEXT_HTML).body(htmlError);
    }
  }

  /**
   * Creates HTML error response.
   *
   * @param status HTTP status
   * @param message Error message
   * @return HTML formatted error response
   */
  private String createErrorHtmlResponse(HttpStatus status, String message) {
    StringBuilder html = new StringBuilder();
    html.append("<!DOCTYPE html>\n");
    html.append("<html>\n");
    html.append("<head>\n");
    html.append("    <title>Error - Checksum Verification</title>\n");
    html.append("    <style>\n");
    html.append("        body { font-family: Arial, sans-serif; margin: 40px; }\n");
    html.append(
        "        .error { background-color: #ffebee; padding: 20px; border-radius: 5px; border-left: 4px solid #f44336; }\n");
    html.append("    </style>\n");
    html.append("</head>\n");
    html.append("<body>\n");
    html.append("    <h1>Checksum Verification System - Error</h1>\n");
    html.append("    <div class=\"error\">\n");
    html.append("        <h2>Error ");
    html.append(status.value());
    html.append("</h2>\n");
    html.append("        <p>");
    html.append(escapeHtml(message));
    html.append("</p>\n");
    html.append("    </div>\n");
    html.append("</body>\n");
    html.append("</html>");
    return html.toString();
  }

  /**
   * Escapes HTML special characters to prevent XSS attacks.
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
        .replace("'", "&#x27;");
  }

  /**
   * Global exception handler for CryptographicException.
   *
   * @param e The cryptographic exception
   * @return Error response
   */
  @ExceptionHandler(CryptographicException.class)
  public ResponseEntity<?> handleCryptographicException(CryptographicException e) {
    return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getUserMessage(), "text/html");
  }

  /**
   * Global exception handler for general exceptions.
   *
   * @param e The exception
   * @return Error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGeneralException(Exception e) {
    return createErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", "text/html");
  }

  /** DTO for JSON hash responses. */
  public static class HashResponseDto {
    private final String originalData;
    private final String algorithm;
    private final String hexHash;
    private final String timestamp;
    private final long computationTimeMs;

    public HashResponseDto(
        String originalData,
        String algorithm,
        String hexHash,
        String timestamp,
        long computationTimeMs) {
      this.originalData = originalData;
      this.algorithm = algorithm;
      this.hexHash = hexHash;
      this.timestamp = timestamp;
      this.computationTimeMs = computationTimeMs;
    }

    public String getOriginalData() {
      return originalData;
    }

    public String getAlgorithm() {
      return algorithm;
    }

    public String getHexHash() {
      return hexHash;
    }

    public String getTimestamp() {
      return timestamp;
    }

    public long getComputationTimeMs() {
      return computationTimeMs;
    }
  }

  /** DTO for JSON error responses. */
  public static class ErrorResponseDto {
    private final int status;
    private final String message;

    public ErrorResponseDto(int status, String message) {
      this.status = status;
      this.message = message;
    }

    public int getStatus() {
      return status;
    }

    public String getMessage() {
      return message;
    }
  }
}
