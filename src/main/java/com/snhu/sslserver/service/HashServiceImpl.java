package com.snhu.sslserver.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;
import com.snhu.sslserver.factory.HashAlgorithmFactory;
import com.snhu.sslserver.model.AlgorithmInfo;
import com.snhu.sslserver.model.HashResult;
import com.snhu.sslserver.model.ValidationResult;
import com.snhu.sslserver.provider.ICryptographicProvider;
import com.snhu.sslserver.strategy.HashAlgorithmStrategy;

/**
 * Implementation of IHashService providing secure hash computation with comprehensive validation
 * and error handling. This service orchestrates input validation, algorithm selection, and hash
 * computation using the Strategy pattern.
 *
 * <p>Key features: - Strategy pattern for algorithm selection - Comprehensive input validation -
 * Security-aware error handling - Performance monitoring - Structured logging without sensitive
 * data exposure
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Service
public class HashServiceImpl implements IHashService {

  private static final Logger logger = LoggerFactory.getLogger(HashServiceImpl.class);

  /** Default algorithm used when no specific algorithm is requested. */
  @Value("${app.hash.default-algorithm:SHA-256}")
  private String defaultAlgorithm;

  /** Student name to be included in hash input data. */
  @Value("${app.student.name:Rick Goshen}")
  private String studentName;

  private final IInputValidator inputValidator;
  private final ICryptographicProvider cryptographicProvider;
  private final HashAlgorithmFactory algorithmFactory;

  /**
   * Constructor with dependency injection.
   *
   * @param inputValidator Validator for input sanitization and validation
   * @param cryptographicProvider Provider for cryptographic operations
   * @param algorithmFactory Factory for creating algorithm strategies
   */
  public HashServiceImpl(
      IInputValidator inputValidator,
      ICryptographicProvider cryptographicProvider,
      HashAlgorithmFactory algorithmFactory) {
    this.inputValidator = Objects.requireNonNull(inputValidator, "Input validator cannot be null");
    this.cryptographicProvider =
        Objects.requireNonNull(cryptographicProvider, "Cryptographic provider cannot be null");
    this.algorithmFactory =
        Objects.requireNonNull(algorithmFactory, "Algorithm factory cannot be null");
  }

  @Override
  public HashResult computeHash(String input, String algorithm) throws CryptographicException {
    Objects.requireNonNull(input, "Input cannot be null");
    Objects.requireNonNull(algorithm, "Algorithm cannot be null");

    long startTime = System.currentTimeMillis();
    String correlationId = generateCorrelationId();

    logger.info(
        "Starting hash computation [{}] - algorithm: {}, input length: {}",
        correlationId,
        algorithm,
        input.length());

    try {
      // Validate and sanitize input
      ValidationResult inputValidation = inputValidator.validateAndSanitize(input);
      if (!inputValidation.isValid()) {
        String errorMessage =
            "Input validation failed: " + String.join(", ", inputValidation.getErrors());
        logger.warn("Input validation failed [{}]: {}", correlationId, errorMessage);
        throw new CryptographicException(ErrorCode.INPUT_VALIDATION_FAILED, errorMessage);
      }

      // Log warnings if present
      if (inputValidation.hasWarnings()) {
        logger.info(
            "Input validation warnings [{}]: {}",
            correlationId,
            String.join(", ", inputValidation.getWarnings()));
      }

      // Validate algorithm
      ValidationResult algorithmValidation = inputValidator.validateAlgorithm(algorithm);
      if (!algorithmValidation.isValid()) {
        String errorMessage =
            "Algorithm validation failed: " + String.join(", ", algorithmValidation.getErrors());
        logger.warn("Algorithm validation failed [{}]: {}", correlationId, errorMessage);
        throw new CryptographicException(ErrorCode.ALGORITHM_NOT_SUPPORTED, errorMessage);
      }

      // Create data string with student name
      String sanitizedInput = inputValidation.getSanitizedData();
      String dataToHash = createDataString(sanitizedInput);

      // Get algorithm strategy
      HashAlgorithmStrategy strategy = algorithmFactory.createStrategy(algorithm);
      logger.debug(
          "Using algorithm strategy [{}]: {} (secure: {}, performance: {})",
          correlationId,
          strategy.getAlgorithmName(),
          strategy.isSecure(),
          strategy.getPerformanceRating());

      // Compute hash
      byte[] hashBytes = strategy.computeHash(dataToHash);
      String hexHash = cryptographicProvider.bytesToHex(hashBytes);

      long computationTime = System.currentTimeMillis() - startTime;
      Instant timestamp = Instant.now();

      // Create result
      HashResult result =
          HashResult.builder()
              .originalData(dataToHash)
              .algorithm(strategy.getAlgorithmName())
              .hexHash(hexHash)
              .timestamp(timestamp)
              .computationTimeMs(computationTime)
              .build();

      logger.info(
          "Hash computation completed successfully [{}] - algorithm: {}, computation time: {}ms",
          correlationId,
          strategy.getAlgorithmName(),
          computationTime);

      return result;

    } catch (CryptographicException e) {
      logger.error("Hash computation failed [{}]: {}", correlationId, e.getMessage());
      throw e;
    } catch (Exception e) {
      logger.error(
          "Unexpected error during hash computation [{}]: {}", correlationId, e.getMessage(), e);
      throw new CryptographicException(
          ErrorCode.COMPUTATION_FAILED, "Hash computation failed due to unexpected error", e);
    }
  }

  @Override
  public HashResult computeHash(String input) throws CryptographicException {
    return computeHash(input, defaultAlgorithm);
  }

  @Override
  public List<AlgorithmInfo> getSupportedAlgorithms() {
    logger.debug("Retrieving supported algorithms information");

    return algorithmFactory.getAllStrategies().stream()
        .filter(HashAlgorithmStrategy::isSecure)
        .map(this::createAlgorithmInfo)
        .collect(Collectors.toList());
  }

  @Override
  public boolean isAlgorithmSecure(String algorithm) {
    if (algorithm == null) {
      return false;
    }

    try {
      HashAlgorithmStrategy strategy = algorithmFactory.createStrategy(algorithm);
      return strategy.isSecure() && cryptographicProvider.isAlgorithmSecure(algorithm);
    } catch (IllegalArgumentException e) {
      logger.debug("Algorithm not supported: {}", algorithm);
      return false;
    }
  }

  @Override
  public String getDefaultAlgorithm() {
    return defaultAlgorithm;
  }

  /**
   * Creates the data string for hashing by combining student name with input data.
   *
   * @param input The sanitized input data
   * @return Formatted data string for hashing
   */
  private String createDataString(String input) {
    // Format: "StudentName: [name] Data: [input]"
    return String.format("StudentName: %s Data: %s", studentName, input);
  }

  /**
   * Creates AlgorithmInfo from a HashAlgorithmStrategy.
   *
   * @param strategy The strategy to convert
   * @return AlgorithmInfo instance
   */
  private AlgorithmInfo createAlgorithmInfo(HashAlgorithmStrategy strategy) {
    return AlgorithmInfo.builder()
        .name(strategy.getAlgorithmName())
        .secure(strategy.isSecure())
        .performance(strategy.getPerformanceRating())
        .description(strategy.getDescription())
        .aliases(java.util.Collections.emptySet()) // No aliases for now
        .build();
  }

  /**
   * Generates a correlation ID for tracking operations across logs.
   *
   * @return Unique correlation ID
   */
  private String generateCorrelationId() {
    return java.util.UUID.randomUUID().toString().substring(0, 8);
  }
}
