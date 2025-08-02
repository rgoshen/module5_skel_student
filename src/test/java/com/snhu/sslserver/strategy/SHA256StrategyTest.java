package com.snhu.sslserver.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;
import com.snhu.sslserver.model.PerformanceRating;
import com.snhu.sslserver.provider.ICryptographicProvider;

/**
 * Unit tests for SHA256Strategy implementation. Tests cover hash computation, error handling, and
 * strategy metadata.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class SHA256StrategyTest {

  @Mock private ICryptographicProvider cryptographicProvider;

  @Mock private MessageDigest messageDigest;

  private SHA256Strategy strategy;

  private static final String TEST_INPUT = "test input data";
  private static final byte[] EXPECTED_HASH = {
    0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF
  };

  @BeforeEach
  void setUp() {
    strategy = new SHA256Strategy(cryptographicProvider);
  }

  @Test
  @DisplayName("Should compute SHA-256 hash successfully")
  void shouldComputeHashSuccessfully() throws Exception {
    // Arrange
    when(cryptographicProvider.createDigest("SHA-256")).thenReturn(messageDigest);
    when(messageDigest.digest(TEST_INPUT.getBytes("UTF-8"))).thenReturn(EXPECTED_HASH);

    // Act
    byte[] result = strategy.computeHash(TEST_INPUT);

    // Assert
    assertThat(result).isEqualTo(EXPECTED_HASH);
  }

  @Test
  @DisplayName("Should throw CryptographicException when digest creation fails")
  void shouldThrowExceptionWhenDigestCreationFails() throws Exception {
    // Arrange
    when(cryptographicProvider.createDigest("SHA-256"))
        .thenThrow(
            new CryptographicException(ErrorCode.ALGORITHM_NOT_SUPPORTED, "SHA-256 not available"));

    // Act & Assert
    assertThatThrownBy(() -> strategy.computeHash(TEST_INPUT))
        .isInstanceOf(CryptographicException.class)
        .hasMessageContaining("Failed to compute SHA-256 hash")
        .extracting("errorCode")
        .isEqualTo(ErrorCode.COMPUTATION_FAILED);
  }

  @Test
  @DisplayName("Should throw CryptographicException for null input")
  void shouldThrowExceptionForNullInput() {
    // Act & Assert
    assertThatThrownBy(() -> strategy.computeHash(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Input cannot be null");
  }

  @Test
  @DisplayName("Should return correct algorithm name")
  void shouldReturnCorrectAlgorithmName() {
    // Act
    String algorithmName = strategy.getAlgorithmName();

    // Assert
    assertThat(algorithmName).isEqualTo("SHA-256");
  }

  @Test
  @DisplayName("Should indicate algorithm is secure")
  void shouldIndicateAlgorithmIsSecure() {
    // Act
    boolean isSecure = strategy.isSecure();

    // Assert
    assertThat(isSecure).isTrue();
  }

  @Test
  @DisplayName("Should return fast performance rating")
  void shouldReturnFastPerformanceRating() {
    // Act
    PerformanceRating rating = strategy.getPerformanceRating();

    // Assert
    assertThat(rating).isEqualTo(PerformanceRating.FAST);
  }

  @Test
  @DisplayName("Should return meaningful description")
  void shouldReturnMeaningfulDescription() {
    // Act
    String description = strategy.getDescription();

    // Assert
    assertThat(description)
        .isNotNull()
        .isNotEmpty()
        .contains("SHA-256")
        .contains("NIST-approved")
        .contains("security-to-performance");
  }

  @Test
  @DisplayName("Should handle empty input string")
  void shouldHandleEmptyInputString() throws Exception {
    // Arrange
    String emptyInput = "";
    when(cryptographicProvider.createDigest("SHA-256")).thenReturn(messageDigest);
    when(messageDigest.digest(emptyInput.getBytes("UTF-8"))).thenReturn(EXPECTED_HASH);

    // Act
    byte[] result = strategy.computeHash(emptyInput);

    // Assert
    assertThat(result).isEqualTo(EXPECTED_HASH);
  }

  @Test
  @DisplayName("Should handle Unicode input correctly")
  void shouldHandleUnicodeInputCorrectly() throws Exception {
    // Arrange
    String unicodeInput = "测试数据 🔒 Тест";
    when(cryptographicProvider.createDigest("SHA-256")).thenReturn(messageDigest);
    when(messageDigest.digest(unicodeInput.getBytes("UTF-8"))).thenReturn(EXPECTED_HASH);

    // Act
    byte[] result = strategy.computeHash(unicodeInput);

    // Assert
    assertThat(result).isEqualTo(EXPECTED_HASH);
  }

  @Test
  @DisplayName("Should throw NullPointerException when cryptographic provider is null")
  void shouldThrowExceptionWhenProviderIsNull() {
    // Act & Assert
    assertThatThrownBy(() -> new SHA256Strategy(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Cryptographic provider cannot be null");
  }
}
