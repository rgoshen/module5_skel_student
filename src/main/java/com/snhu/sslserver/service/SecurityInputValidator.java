package com.snhu.sslserver.service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.snhu.sslserver.model.ValidationResult;
import com.snhu.sslserver.provider.ICryptographicProvider;

/**
 * Security-focused implementation of input validation and sanitization. This class provides
 * comprehensive validation for hash input data and algorithm names to prevent security
 * vulnerabilities and ensure data integrity.
 *
 * <p>Key security features: - Length validation to prevent DoS attacks - Character encoding
 * validation for UTF-8 compatibility - Content sanitization to remove potentially harmful
 * characters - Algorithm security validation against deprecated/insecure algorithms - Input
 * normalization for consistent processing
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Service
public class SecurityInputValidator implements IInputValidator {

  /** Maximum allowed input length to prevent memory exhaustion attacks. */
  @Value("${app.hash.max-input-length:10000}")
  private int maxInputLength;

  /** Minimum required input length to ensure meaningful data. */
  @Value("${app.hash.min-input-length:1}")
  private int minInputLength;

  /** Cryptographic provider for algorithm validation. */
  private final ICryptographicProvider cryptographicProvider;

  /**
   * Pattern for validating safe characters in input data. Allows: - Alphanumeric characters
   * (Unicode) - Common punctuation and symbols - Whitespace characters - Excludes control
   * characters and potentially dangerous sequences
   */
  private static final Pattern SAFE_CONTENT_PATTERN =
      Pattern.compile("^[\\p{L}\\p{N}\\p{P}\\p{S}\\p{Z}\\p{M}]*$");

  /**
   * Pattern for validating algorithm names. Allows: - Letters, numbers, hyphens, and underscores -
   * Case insensitive matching
   */
  private static final Pattern ALGORITHM_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

  /**
   * Characters that should be normalized or removed during sanitization. These include various
   * Unicode whitespace and control characters.
   */
  private static final String CHARACTERS_TO_NORMALIZE =
      "\u00A0\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u200C\u200D\u2028\u2029\u202F\u205F\u3000\uFEFF";

  /**
   * Constructor with dependency injection.
   *
   * @param cryptographicProvider Provider for algorithm validation
   */
  public SecurityInputValidator(ICryptographicProvider cryptographicProvider) {
    this.cryptographicProvider =
        Objects.requireNonNull(cryptographicProvider, "Cryptographic provider cannot be null");
  }

  @Override
  public ValidationResult validateAndSanitize(String input) {
    if (input == null) {
      return ValidationResult.failure("Input cannot be null");
    }

    List<String> errors = new ArrayList<>();
    List<String> warnings = new ArrayList<>();

    // Validate input length
    ValidationResult lengthResult = validateInputLength(input);
    if (!lengthResult.isValid()) {
      errors.addAll(lengthResult.getErrors());
    }

    // Validate input content
    ValidationResult contentResult = validateInputContent(input);
    if (!contentResult.isValid()) {
      errors.addAll(contentResult.getErrors());
    }
    warnings.addAll(contentResult.getWarnings());

    // If validation failed, return failure result
    if (!errors.isEmpty()) {
      return ValidationResult.failure(errors, warnings);
    }

    // Sanitize the input
    String sanitized = sanitizeInput(input);

    // Return success with sanitized data
    if (warnings.isEmpty()) {
      return ValidationResult.success(sanitized);
    } else {
      return ValidationResult.successWithWarnings(sanitized, warnings);
    }
  }

  @Override
  public ValidationResult validateAlgorithm(String algorithm) {
    if (algorithm == null || algorithm.trim().isEmpty()) {
      return ValidationResult.failure("Algorithm name cannot be null or empty");
    }

    String trimmedAlgorithm = algorithm.trim();
    List<String> errors = new ArrayList<>();

    // Validate algorithm name format
    if (!ALGORITHM_NAME_PATTERN.matcher(trimmedAlgorithm).matches()) {
      errors.add(
          "Algorithm name contains invalid characters. Only letters, numbers, hyphens, and underscores are allowed");
    }

    // Normalize algorithm name to uppercase for consistent comparison
    String normalizedAlgorithm = trimmedAlgorithm.toUpperCase();

    // Check if algorithm is deprecated/insecure
    if (cryptographicProvider.getDeprecatedAlgorithms().contains(normalizedAlgorithm)) {
      errors.add(
          String.format(
              "Algorithm '%s' is deprecated and insecure. Use SHA-256 or newer", algorithm));
    }

    // Check if algorithm is secure and supported
    if (!cryptographicProvider.isAlgorithmSecure(normalizedAlgorithm)) {
      errors.add(
          String.format(
              "Algorithm '%s' is not secure or supported. Supported algorithms: %s",
              algorithm, String.join(", ", cryptographicProvider.getSecureAlgorithms())));
    }

    if (!errors.isEmpty()) {
      return ValidationResult.failure(errors);
    }

    return ValidationResult.success(trimmedAlgorithm);
  }

  @Override
  public ValidationResult validateInputLength(String input) {
    if (input == null) {
      return ValidationResult.failure("Input cannot be null");
    }

    int length = input.length();

    if (length < minInputLength) {
      return ValidationResult.failure(
          String.format(
              "Input length (%d) is below minimum required length (%d)", length, minInputLength));
    }

    if (length > maxInputLength) {
      return ValidationResult.failure(
          String.format(
              "Input length (%d) exceeds maximum allowed length (%d)", length, maxInputLength));
    }

    return ValidationResult.success(input);
  }

  @Override
  public ValidationResult validateInputContent(String input) {
    if (input == null) {
      return ValidationResult.failure("Input cannot be null");
    }

    List<String> errors = new ArrayList<>();
    List<String> warnings = new ArrayList<>();

    // Check for valid UTF-8 encoding
    try {
      byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
      String reconstructed = new String(bytes, StandardCharsets.UTF_8);
      if (!input.equals(reconstructed)) {
        errors.add("Input contains invalid UTF-8 encoding");
      }
    } catch (Exception e) {
      errors.add("Input encoding validation failed: " + e.getMessage());
    }

    // Check for safe content pattern
    if (!SAFE_CONTENT_PATTERN.matcher(input).matches()) {
      errors.add("Input contains unsafe characters or control sequences");
    }

    // Check for potentially problematic sequences
    if (input.contains("\0")) {
      errors.add("Input contains null bytes which are not allowed");
    }

    // Check for excessive whitespace (warning only)
    if (input.trim().length() < input.length() * 0.5) {
      warnings.add("Input contains excessive whitespace which will be normalized");
    }

    // Check for Unicode normalization issues
    if (containsUnnormalizedUnicode(input)) {
      warnings.add("Input contains Unicode characters that will be normalized");
    }

    if (!errors.isEmpty()) {
      return ValidationResult.failure(errors, warnings);
    }

    if (!warnings.isEmpty()) {
      return ValidationResult.successWithWarnings(input, warnings);
    }

    return ValidationResult.success(input);
  }

  @Override
  public int getMaxInputLength() {
    return maxInputLength;
  }

  @Override
  public int getMinInputLength() {
    return minInputLength;
  }

  /**
   * Sanitizes input data by normalizing whitespace and removing potentially harmful characters.
   * This method ensures consistent processing while maintaining data integrity.
   *
   * @param input Raw input data
   * @return Sanitized input data
   */
  private String sanitizeInput(String input) {
    if (input == null) {
      return null;
    }

    String sanitized = input;

    // Normalize Unicode characters
    sanitized = java.text.Normalizer.normalize(sanitized, java.text.Normalizer.Form.NFC);

    // Replace various Unicode whitespace characters with standard space
    for (char c : CHARACTERS_TO_NORMALIZE.toCharArray()) {
      sanitized = sanitized.replace(c, ' ');
    }

    // Normalize multiple consecutive whitespace to single space
    sanitized = sanitized.replaceAll("\\s+", " ");

    // Trim leading and trailing whitespace
    sanitized = sanitized.trim();

    return sanitized;
  }

  /**
   * Checks if the input contains Unicode characters that need normalization.
   *
   * @param input Input string to check
   * @return true if input contains unnormalized Unicode characters
   */
  private boolean containsUnnormalizedUnicode(String input) {
    if (input == null) {
      return false;
    }

    // Check if normalization would change the string
    String normalized = java.text.Normalizer.normalize(input, java.text.Normalizer.Form.NFC);
    return !input.equals(normalized);
  }
}
