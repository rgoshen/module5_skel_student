package com.snhu.sslserver.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.security.MessageDigest;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;

/**
 * Unit tests for CryptographicProvider class. Tests cover algorithm validation, MessageDigest
 * creation, hex conversion, and security policy enforcement.
 *
 * @author Rick Goshen
 * @version 1.0
 */
class CryptographicProviderTest {

  private CryptographicProvider cryptographicProvider;

  @BeforeEach
  void setUp() {
    cryptographicProvider = new CryptographicProvider();
  }

  @Test
  @DisplayName("Should create MessageDigest for SHA-256 algorithm")
  void shouldCreateMessageDigestForSHA256() throws CryptographicException {
    // When
    MessageDigest digest = cryptographicProvider.createDigest("SHA-256");

    // Then
    assertThat(digest).isNotNull();
    assertThat(digest.getAlgorithm()).isEqualTo("SHA-256");
  }

  @Test
  @DisplayName("Should create MessageDigest for all available secure algorithms")
  void shouldCreateMessageDigestForAvailableSecureAlgorithms() throws CryptographicException {
    // Given
    Set<String> secureAlgorithms = cryptographicProvider.getSecureAlgorithms();

    // When/Then
    for (String algorithm : secureAlgorithms) {
      MessageDigest digest = cryptographicProvider.createDigest(algorithm);
      assertThat(digest).isNotNull();
      assertThat(digest.getAlgorithm()).isEqualTo(algorithm);
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"sha-256", "Sha-256", "SHA-256", "  SHA-256  "})
  @DisplayName("Should handle case insensitive and whitespace in algorithm names")
  void shouldHandleCaseInsensitiveAlgorithmNames(String algorithm) throws CryptographicException {
    // When
    MessageDigest digest = cryptographicProvider.createDigest(algorithm);

    // Then
    assertThat(digest).isNotNull();
    assertThat(digest.getAlgorithm()).isEqualTo("SHA-256");
  }

  @ParameterizedTest
  @ValueSource(strings = {"MD5", "SHA-1", "md5", "sha-1"})
  @DisplayName("Should reject deprecated algorithms")
  void shouldRejectDeprecatedAlgorithms(String algorithm) {
    // When/Then
    assertThatThrownBy(() -> cryptographicProvider.createDigest(algorithm))
        .isInstanceOf(CryptographicException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALGORITHM_INSECURE)
        .hasMessageContaining("deprecated and insecure");
  }

  @ParameterizedTest
  @ValueSource(strings = {"INVALID", "SHA-999", "UNKNOWN"})
  @DisplayName("Should reject unsupported algorithms")
  void shouldRejectUnsupportedAlgorithms(String algorithm) {
    // When/Then
    assertThatThrownBy(() -> cryptographicProvider.createDigest(algorithm))
        .isInstanceOf(CryptographicException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALGORITHM_NOT_SUPPORTED)
        .hasMessageContaining("not supported");
  }

  @Test
  @DisplayName("Should throw exception for null algorithm")
  void shouldThrowExceptionForNullAlgorithm() {
    // When/Then
    assertThatThrownBy(() -> cryptographicProvider.createDigest(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Algorithm cannot be null");
  }

  @Test
  @DisplayName("Should convert bytes to hex correctly")
  void shouldConvertBytesToHexCorrectly() {
    // Given
    byte[] testBytes = {0x00, 0x01, 0x0F, 0x10, (byte) 0xFF, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
    String expectedHex = "00010f10ffabcdef";

    // When
    String actualHex = cryptographicProvider.bytesToHex(testBytes);

    // Then
    assertThat(actualHex).isEqualTo(expectedHex);
  }

  @Test
  @DisplayName("Should convert empty byte array to empty string")
  void shouldConvertEmptyByteArrayToEmptyString() {
    // Given
    byte[] emptyBytes = {};

    // When
    String result = cryptographicProvider.bytesToHex(emptyBytes);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should throw exception for null byte array")
  void shouldThrowExceptionForNullByteArray() {
    // When/Then
    assertThatThrownBy(() -> cryptographicProvider.bytesToHex(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Byte array cannot be null");
  }

  @Test
  @DisplayName("Should return non-empty set of secure algorithms")
  void shouldReturnNonEmptySetOfSecureAlgorithms() {
    // When
    Set<String> secureAlgorithms = cryptographicProvider.getSecureAlgorithms();

    // Then
    assertThat(secureAlgorithms)
        .isNotEmpty()
        .contains("SHA-256") // SHA-256 should always be available
        .allMatch(alg -> alg.startsWith("SHA-")); // All should be SHA variants
  }

  @Test
  @DisplayName("Should return immutable set of secure algorithms")
  void shouldReturnImmutableSetOfSecureAlgorithms() {
    // When
    Set<String> secureAlgorithms = cryptographicProvider.getSecureAlgorithms();

    // Then
    assertThatThrownBy(() -> secureAlgorithms.add("NEW-ALGORITHM"))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("Should return correct set of deprecated algorithms")
  void shouldReturnCorrectSetOfDeprecatedAlgorithms() {
    // When
    Set<String> deprecatedAlgorithms = cryptographicProvider.getDeprecatedAlgorithms();

    // Then
    assertThat(deprecatedAlgorithms).containsExactlyInAnyOrder("MD5", "SHA-1").hasSize(2);
  }

  @Test
  @DisplayName("Should return immutable set of deprecated algorithms")
  void shouldReturnImmutableSetOfDeprecatedAlgorithms() {
    // When
    Set<String> deprecatedAlgorithms = cryptographicProvider.getDeprecatedAlgorithms();

    // Then
    assertThatThrownBy(() -> deprecatedAlgorithms.add("NEW-DEPRECATED"))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("Should report available secure algorithms as available")
  void shouldReportAvailableSecureAlgorithmsAsAvailable() {
    // Given
    Set<String> secureAlgorithms = cryptographicProvider.getSecureAlgorithms();

    // When/Then
    for (String algorithm : secureAlgorithms) {
      boolean isAvailable = cryptographicProvider.isAlgorithmAvailable(algorithm);
      assertThat(isAvailable).isTrue();
    }
  }

  @Test
  @DisplayName("Should report unknown algorithm as not available")
  void shouldReportUnknownAlgorithmAsNotAvailable() {
    // When
    boolean isAvailable = cryptographicProvider.isAlgorithmAvailable("UNKNOWN-ALGORITHM");

    // Then
    assertThat(isAvailable).isFalse();
  }

  @Test
  @DisplayName("Should throw exception for null algorithm in availability check")
  void shouldThrowExceptionForNullAlgorithmInAvailabilityCheck() {
    // When/Then
    assertThatThrownBy(() -> cryptographicProvider.isAlgorithmAvailable(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Algorithm cannot be null");
  }

  @Test
  @DisplayName("Should report available secure algorithms as secure")
  void shouldReportAvailableSecureAlgorithmsAsSecure() {
    // Given
    Set<String> secureAlgorithms = cryptographicProvider.getSecureAlgorithms();

    // When/Then
    for (String algorithm : secureAlgorithms) {
      boolean isSecure = cryptographicProvider.isAlgorithmSecure(algorithm);
      assertThat(isSecure).isTrue();
    }
  }

  @ParameterizedTest
  @ValueSource(strings = {"MD5", "SHA-1", "md5", "sha-1"})
  @DisplayName("Should report deprecated algorithms as not secure")
  void shouldReportDeprecatedAlgorithmsAsNotSecure(String algorithm) {
    // When
    boolean isSecure = cryptographicProvider.isAlgorithmSecure(algorithm);

    // Then
    assertThat(isSecure).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"UNKNOWN", "INVALID", "SHA-999"})
  @DisplayName("Should report unknown algorithms as not secure")
  void shouldReportUnknownAlgorithmsAsNotSecure(String algorithm) {
    // When
    boolean isSecure = cryptographicProvider.isAlgorithmSecure(algorithm);

    // Then
    assertThat(isSecure).isFalse();
  }

  @Test
  @DisplayName("Should throw exception for null algorithm in security check")
  void shouldThrowExceptionForNullAlgorithmInSecurityCheck() {
    // When/Then
    assertThatThrownBy(() -> cryptographicProvider.isAlgorithmSecure(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Algorithm cannot be null");
  }

  @Test
  @DisplayName("Should handle case insensitive algorithm security check")
  void shouldHandleCaseInsensitiveAlgorithmSecurityCheck() {
    // When/Then
    assertThat(cryptographicProvider.isAlgorithmSecure("sha-256")).isTrue();
    assertThat(cryptographicProvider.isAlgorithmSecure("Sha-256")).isTrue();
    assertThat(cryptographicProvider.isAlgorithmSecure("SHA-256")).isTrue();
    assertThat(cryptographicProvider.isAlgorithmSecure("  SHA-256  ")).isTrue();
  }

  @Test
  @DisplayName("Should produce consistent hex output for same input")
  void shouldProduceConsistentHexOutputForSameInput() {
    // Given
    byte[] testBytes = {0x12, 0x34, 0x56, 0x78, (byte) 0x9A, (byte) 0xBC, (byte) 0xDE, (byte) 0xF0};

    // When
    String hex1 = cryptographicProvider.bytesToHex(testBytes);
    String hex2 = cryptographicProvider.bytesToHex(testBytes);

    // Then
    assertThat(hex1).isEqualTo(hex2);
    assertThat(hex1).isEqualTo("123456789abcdef0");
  }
}
