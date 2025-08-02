package com.snhu.sslserver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;
import com.snhu.sslserver.factory.HashAlgorithmFactory;
import com.snhu.sslserver.model.AlgorithmInfo;
import com.snhu.sslserver.model.HashResult;
import com.snhu.sslserver.model.PerformanceRating;
import com.snhu.sslserver.model.ValidationResult;
import com.snhu.sslserver.provider.ICryptographicProvider;
import com.snhu.sslserver.strategy.HashAlgorithmStrategy;

/**
 * Comprehensive unit tests for HashServiceImpl. Tests cover successful hash computation, error
 * handling, input validation integration, algorithm selection, and security validation.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class HashServiceImplTest {

  @Mock private IInputValidator inputValidator;

  @Mock private ICryptographicProvider cryptographicProvider;

  @Mock private HashAlgorithmFactory algorithmFactory;

  @Mock private HashAlgorithmStrategy mockStrategy;

  private HashServiceImpl hashService;

  private static final String TEST_INPUT = "test input data";
  private static final String TEST_ALGORITHM = "SHA-256";
  private static final String SANITIZED_INPUT = "sanitized test input data";
  private static final String STUDENT_NAME = "Rick Goshen";
  private static final String EXPECTED_DATA_STRING =
      "StudentName: " + STUDENT_NAME + " Data: " + SANITIZED_INPUT;
  private static final byte[] TEST_HASH_BYTES = {0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB};
  private static final String TEST_HEX_HASH = "0123456789ab";

  @BeforeEach
  void setUp() {
    hashService = new HashServiceImpl(inputValidator, cryptographicProvider, algorithmFactory);

    // Set up default configuration values
    ReflectionTestUtils.setField(hashService, "defaultAlgorithm", "SHA-256");
    ReflectionTestUtils.setField(hashService, "studentName", STUDENT_NAME);
  }

  @Test
  @DisplayName("Should compute hash successfully with valid input and algorithm")
  void shouldComputeHashSuccessfully() throws CryptographicException {
    // Arrange
    ValidationResult inputValidation = ValidationResult.success(SANITIZED_INPUT);
    ValidationResult algorithmValidation = ValidationResult.success(TEST_ALGORITHM);

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(inputValidation);
    when(inputValidator.validateAlgorithm(TEST_ALGORITHM)).thenReturn(algorithmValidation);
    when(algorithmFactory.createStrategy(TEST_ALGORITHM)).thenReturn(mockStrategy);
    when(mockStrategy.getAlgorithmName()).thenReturn(TEST_ALGORITHM);
    when(mockStrategy.isSecure()).thenReturn(true);
    when(mockStrategy.getPerformanceRating()).thenReturn(PerformanceRating.FAST);
    when(mockStrategy.computeHash(EXPECTED_DATA_STRING)).thenReturn(TEST_HASH_BYTES);
    when(cryptographicProvider.bytesToHex(TEST_HASH_BYTES)).thenReturn(TEST_HEX_HASH);

    // Act
    HashResult result = hashService.computeHash(TEST_INPUT, TEST_ALGORITHM);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getOriginalData()).isEqualTo(EXPECTED_DATA_STRING);
    assertThat(result.getAlgorithm()).isEqualTo(TEST_ALGORITHM);
    assertThat(result.getHexHash()).isEqualTo(TEST_HEX_HASH);
    assertThat(result.getTimestamp()).isNotNull();
    assertThat(result.getComputationTimeMs()).isGreaterThanOrEqualTo(0);

    // Verify interactions
    verify(inputValidator).validateAndSanitize(TEST_INPUT);
    verify(inputValidator).validateAlgorithm(TEST_ALGORITHM);
    verify(algorithmFactory).createStrategy(TEST_ALGORITHM);
    verify(mockStrategy).computeHash(EXPECTED_DATA_STRING);
    verify(cryptographicProvider).bytesToHex(TEST_HASH_BYTES);
  }

  @Test
  @DisplayName("Should use default algorithm when algorithm not specified")
  void shouldUseDefaultAlgorithmWhenNotSpecified() throws CryptographicException {
    // Arrange
    ValidationResult inputValidation = ValidationResult.success(SANITIZED_INPUT);
    ValidationResult algorithmValidation = ValidationResult.success("SHA-256");

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(inputValidation);
    when(inputValidator.validateAlgorithm("SHA-256")).thenReturn(algorithmValidation);
    when(algorithmFactory.createStrategy("SHA-256")).thenReturn(mockStrategy);
    when(mockStrategy.getAlgorithmName()).thenReturn("SHA-256");
    when(mockStrategy.computeHash(anyString())).thenReturn(TEST_HASH_BYTES);
    when(cryptographicProvider.bytesToHex(TEST_HASH_BYTES)).thenReturn(TEST_HEX_HASH);

    // Act
    HashResult result = hashService.computeHash(TEST_INPUT);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getAlgorithm()).isEqualTo("SHA-256");

    // Verify default algorithm was used
    verify(algorithmFactory).createStrategy("SHA-256");
  }

  @Test
  @DisplayName("Should throw CryptographicException when input validation fails")
  void shouldThrowExceptionWhenInputValidationFails() {
    // Arrange
    ValidationResult failedValidation =
        ValidationResult.failure(Arrays.asList("Input too long", "Invalid characters"));

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(failedValidation);

    // Act & Assert
    assertThatThrownBy(() -> hashService.computeHash(TEST_INPUT, TEST_ALGORITHM))
        .isInstanceOf(CryptographicException.class)
        .hasMessageContaining("Input validation failed")
        .extracting("errorCode")
        .isEqualTo(ErrorCode.INPUT_VALIDATION_FAILED);

    // Verify no further processing occurred
    verify(inputValidator).validateAndSanitize(TEST_INPUT);
  }

  @Test
  @DisplayName("Should throw CryptographicException when algorithm validation fails")
  void shouldThrowExceptionWhenAlgorithmValidationFails() {
    // Arrange
    ValidationResult inputValidation = ValidationResult.success(SANITIZED_INPUT);
    ValidationResult algorithmValidation =
        ValidationResult.failure("Algorithm 'MD5' is deprecated and insecure");

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(inputValidation);
    when(inputValidator.validateAlgorithm("MD5")).thenReturn(algorithmValidation);

    // Act & Assert
    assertThatThrownBy(() -> hashService.computeHash(TEST_INPUT, "MD5"))
        .isInstanceOf(CryptographicException.class)
        .hasMessageContaining("Algorithm validation failed")
        .extracting("errorCode")
        .isEqualTo(ErrorCode.ALGORITHM_NOT_SUPPORTED);

    // Verify validation was attempted
    verify(inputValidator).validateAlgorithm("MD5");
  }

  @Test
  @DisplayName("Should handle input validation warnings gracefully")
  void shouldHandleInputValidationWarningsGracefully() throws CryptographicException {
    // Arrange
    ValidationResult inputValidation =
        ValidationResult.successWithWarnings(
            SANITIZED_INPUT, Arrays.asList("Input contains excessive whitespace"));
    ValidationResult algorithmValidation = ValidationResult.success(TEST_ALGORITHM);

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(inputValidation);
    when(inputValidator.validateAlgorithm(TEST_ALGORITHM)).thenReturn(algorithmValidation);
    when(algorithmFactory.createStrategy(TEST_ALGORITHM)).thenReturn(mockStrategy);
    when(mockStrategy.getAlgorithmName()).thenReturn(TEST_ALGORITHM);
    when(mockStrategy.computeHash(anyString())).thenReturn(TEST_HASH_BYTES);
    when(cryptographicProvider.bytesToHex(TEST_HASH_BYTES)).thenReturn(TEST_HEX_HASH);

    // Act
    HashResult result = hashService.computeHash(TEST_INPUT, TEST_ALGORITHM);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getHexHash()).isEqualTo(TEST_HEX_HASH);
  }

  @Test
  @DisplayName("Should throw CryptographicException when strategy computation fails")
  void shouldThrowExceptionWhenStrategyComputationFails() throws CryptographicException {
    // Arrange
    ValidationResult inputValidation = ValidationResult.success(SANITIZED_INPUT);
    ValidationResult algorithmValidation = ValidationResult.success(TEST_ALGORITHM);

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(inputValidation);
    when(inputValidator.validateAlgorithm(TEST_ALGORITHM)).thenReturn(algorithmValidation);
    when(algorithmFactory.createStrategy(TEST_ALGORITHM)).thenReturn(mockStrategy);
    when(mockStrategy.getAlgorithmName()).thenReturn(TEST_ALGORITHM);
    when(mockStrategy.computeHash(anyString()))
        .thenThrow(
            new CryptographicException(ErrorCode.COMPUTATION_FAILED, "Hash computation failed"));

    // Act & Assert
    assertThatThrownBy(() -> hashService.computeHash(TEST_INPUT, TEST_ALGORITHM))
        .isInstanceOf(CryptographicException.class)
        .hasMessageContaining("Hash computation failed")
        .extracting("errorCode")
        .isEqualTo(ErrorCode.COMPUTATION_FAILED);
  }

  @Test
  @DisplayName("Should throw CryptographicException when algorithm factory throws exception")
  void shouldThrowExceptionWhenAlgorithmFactoryFails() {
    // Arrange
    ValidationResult inputValidation = ValidationResult.success(SANITIZED_INPUT);
    ValidationResult algorithmValidation = ValidationResult.success("UNSUPPORTED");

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(inputValidation);
    when(inputValidator.validateAlgorithm("UNSUPPORTED")).thenReturn(algorithmValidation);
    when(algorithmFactory.createStrategy("UNSUPPORTED"))
        .thenThrow(new IllegalArgumentException("Unsupported algorithm"));

    // Act & Assert
    assertThatThrownBy(() -> hashService.computeHash(TEST_INPUT, "UNSUPPORTED"))
        .isInstanceOf(CryptographicException.class)
        .hasMessageContaining("Hash computation failed due to unexpected error")
        .extracting("errorCode")
        .isEqualTo(ErrorCode.COMPUTATION_FAILED);
  }

  @Test
  @DisplayName("Should return supported algorithms information")
  void shouldReturnSupportedAlgorithmsInformation() {
    // Arrange
    HashAlgorithmStrategy strategy1 = createMockStrategy("SHA-256", true, PerformanceRating.FAST);
    HashAlgorithmStrategy strategy2 =
        createMockStrategy("SHA-3-256", true, PerformanceRating.MEDIUM);
    HashAlgorithmStrategy insecureStrategy =
        createMockStrategy("MD5", false, PerformanceRating.FAST);

    when(algorithmFactory.getAllStrategies())
        .thenReturn(Arrays.asList(strategy1, strategy2, insecureStrategy));

    // Act
    List<AlgorithmInfo> algorithms = hashService.getSupportedAlgorithms();

    // Assert
    assertThat(algorithms).hasSize(2); // Only secure algorithms
    assertThat(algorithms)
        .extracting(AlgorithmInfo::getName)
        .containsExactlyInAnyOrder("SHA-256", "SHA-3-256");
    assertThat(algorithms).allMatch(AlgorithmInfo::isSecure);
  }

  @Test
  @DisplayName("Should validate algorithm security correctly")
  void shouldValidateAlgorithmSecurityCorrectly() {
    // Arrange
    when(algorithmFactory.createStrategy("SHA-256")).thenReturn(mockStrategy);
    when(mockStrategy.isSecure()).thenReturn(true);
    when(cryptographicProvider.isAlgorithmSecure("SHA-256")).thenReturn(true);

    when(algorithmFactory.createStrategy("MD5"))
        .thenThrow(new IllegalArgumentException("Unsupported"));

    // Act & Assert
    assertThat(hashService.isAlgorithmSecure("SHA-256")).isTrue();
    assertThat(hashService.isAlgorithmSecure("MD5")).isFalse();
    assertThat(hashService.isAlgorithmSecure(null)).isFalse();
  }

  @Test
  @DisplayName("Should return default algorithm")
  void shouldReturnDefaultAlgorithm() {
    // Act
    String defaultAlgorithm = hashService.getDefaultAlgorithm();

    // Assert
    assertThat(defaultAlgorithm).isEqualTo("SHA-256");
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException for null input")
  void shouldThrowExceptionForNullInput() {
    // Act & Assert
    assertThatThrownBy(() -> hashService.computeHash(null, TEST_ALGORITHM))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Input cannot be null");
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException for null algorithm")
  void shouldThrowExceptionForNullAlgorithm() {
    // Act & Assert
    assertThatThrownBy(() -> hashService.computeHash(TEST_INPUT, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Algorithm cannot be null");
  }

  @Test
  @DisplayName("Should format data string correctly with student name")
  void shouldFormatDataStringCorrectlyWithStudentName() throws CryptographicException {
    // Arrange
    ValidationResult inputValidation = ValidationResult.success(SANITIZED_INPUT);
    ValidationResult algorithmValidation = ValidationResult.success(TEST_ALGORITHM);

    when(inputValidator.validateAndSanitize(TEST_INPUT)).thenReturn(inputValidation);
    when(inputValidator.validateAlgorithm(TEST_ALGORITHM)).thenReturn(algorithmValidation);
    when(algorithmFactory.createStrategy(TEST_ALGORITHM)).thenReturn(mockStrategy);
    when(mockStrategy.getAlgorithmName()).thenReturn(TEST_ALGORITHM);
    when(mockStrategy.computeHash(anyString())).thenReturn(TEST_HASH_BYTES);
    when(cryptographicProvider.bytesToHex(TEST_HASH_BYTES)).thenReturn(TEST_HEX_HASH);

    // Act
    HashResult result = hashService.computeHash(TEST_INPUT, TEST_ALGORITHM);

    // Assert
    assertThat(result.getOriginalData()).isEqualTo(EXPECTED_DATA_STRING);
    verify(mockStrategy).computeHash(EXPECTED_DATA_STRING);
  }

  /** Helper method to create mock strategy with specified properties. */
  private HashAlgorithmStrategy createMockStrategy(
      String name, boolean secure, PerformanceRating performance) {
    HashAlgorithmStrategy strategy =
        org.mockito.Mockito.mock(
            HashAlgorithmStrategy.class, org.mockito.Mockito.withSettings().lenient());
    when(strategy.getAlgorithmName()).thenReturn(name);
    when(strategy.isSecure()).thenReturn(secure);
    when(strategy.getPerformanceRating()).thenReturn(performance);
    when(strategy.getDescription()).thenReturn("Test description for " + name);
    return strategy;
  }
}
