package com.snhu.sslserver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.snhu.sslserver.model.ValidationResult;
import com.snhu.sslserver.provider.ICryptographicProvider;

/** Comprehensive unit tests for SecurityInputValidator. */
@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityInputValidator Tests")
class SecurityInputValidatorTest {

  @Mock private ICryptographicProvider cryptographicProvider;

  private SecurityInputValidator validator;

  @BeforeEach
  void setUp() {
    // Setup lenient mock behavior to avoid unnecessary stubbing errors
    lenient()
        .when(cryptographicProvider.getSecureAlgorithms())
        .thenReturn(
            new HashSet<>(
                Arrays.asList(
                    "SHA-256", "SHA-384", "SHA-512", "SHA-3-256", "SHA-3-384", "SHA-3-512")));

    lenient()
        .when(cryptographicProvider.getDeprecatedAlgorithms())
        .thenReturn(new HashSet<>(Arrays.asList("MD5", "SHA-1")));

    validator = new SecurityInputValidator(cryptographicProvider);

    // Set test configuration values
    ReflectionTestUtils.setField(validator, "maxInputLength", 1000);
    ReflectionTestUtils.setField(validator, "minInputLength", 1);
  }

  @Nested
  @DisplayName("Input Validation and Sanitization Tests")
  class InputValidationTests {

    @Test
    @DisplayName("Should validate and sanitize normal input successfully")
    void shouldValidateAndSanitizeNormalInput() {
      String input = "Hello World 123";

      ValidationResult result = validator.validateAndSanitize(input);

      assertThat(result.isValid()).isTrue();
      assertThat(result.getSanitizedData()).isEqualTo("Hello World 123");
      assertThat(result.hasErrors()).isFalse();
      assertThat(result.hasWarnings()).isFalse();
    }

    @Test
    @DisplayName("Should reject null input")
    void shouldRejectNullInput() {
      ValidationResult result = validator.validateAndSanitize(null);

      assertThat(result.isValid()).isFalse();
      assertThat(result.getErrors()).contains("Input cannot be null");
    }

    @Test
    @DisplayName("Should sanitize input with excessive whitespace")
    void shouldSanitizeInputWithExcessiveWhitespace() {
      String input = "   a   "; // Input that is mostly whitespace to trigger warning

      ValidationResult result = validator.validateAndSanitize(input);

      assertThat(result.isValid()).isTrue();
      assertThat(result.getSanitizedData()).isEqualTo("a");
      assertThat(result.hasWarnings()).isTrue();
      assertThat(result.getWarnings()).anyMatch(w -> w.contains("excessive whitespace"));
    }

    @Test
    @DisplayName("Should handle Unicode whitespace characters")
    void shouldHandleUnicodeWhitespace() {
      // U+00A0 NO-BREAK SPACE, U+2003 EM SPACE
      String input = "\u00A0Hello\u2003World\u00A0";
      ValidationResult result = validator.validateAndSanitize(input);
      assertThat(result.isValid()).isTrue();
      // Should trim or normalize whitespace
      assertThat(result.getSanitizedData()).isEqualTo("Hello World");
    }

    @Test
    @DisplayName("Should normalize combining characters")
    void shouldNormalizeCombiningCharacters() {
      // "e" + COMBINING ACUTE ACCENT (U+0301)
      String input = "Cafe\u0301";
      ValidationResult result = validator.validateAndSanitize(input);
      assertThat(result.isValid()).isTrue();
      // Should normalize to single codepoint if NFC normalization is applied
      assertThat(result.getSanitizedData()).isEqualTo("Café");
    }

    @Test
    @DisplayName("Should accept non-ASCII symbols and emoji")
    void shouldAcceptNonAsciiSymbolsAndEmoji() {
      String input = "こんにちは🌟";
      ValidationResult result = validator.validateAndSanitize(input);
      assertThat(result.isValid()).isTrue();
      assertThat(result.getSanitizedData()).isEqualTo("こんにちは🌟");
    }

    @Test
    @DisplayName("Should handle special characters and punctuation")
    void shouldHandleSpecialCharactersAndPunctuation() {
      String input = "Hello! @#$%^&*()_+-=[]{}|;':\",./<>?";

      ValidationResult result = validator.validateAndSanitize(input);

      assertThat(result.isValid()).isTrue();
      assertThat(result.getSanitizedData()).isEqualTo(input);
    }

    @Test
    @DisplayName("Should reject input with null bytes")
    void shouldRejectInputWithNullBytes() {
      String input = "Hello\0World";

      ValidationResult result = validator.validateAndSanitize(input);

      assertThat(result.isValid()).isFalse();
      assertThat(result.getErrors()).anyMatch(e -> e.contains("null bytes"));
    }
  }

  @Nested
  @DisplayName("Input Length Validation Tests")
  class InputLengthTests {

    @Test
    @DisplayName("Should accept input within valid length range")
    void shouldAcceptInputWithinValidLengthRange() {
      String input = "Valid length input";

      ValidationResult result = validator.validateInputLength(input);

      assertThat(result.isValid()).isTrue();
      assertThat(result.getSanitizedData()).isEqualTo(input);
    }

    @Test
    @DisplayName("Should reject input above maximum length")
    void shouldRejectInputAboveMaximumLength() {
      // Set maximum length to 10 for this test
      ReflectionTestUtils.setField(validator, "maxInputLength", 10);
      String input = "This input is definitely too long for the maximum";

      ValidationResult result = validator.validateInputLength(input);

      assertThat(result.isValid()).isFalse();
      assertThat(result.getErrors()).anyMatch(e -> e.contains("exceeds maximum allowed length"));
    }

    @Test
    @DisplayName("Should return correct max input length")
    void shouldReturnCorrectMaxInputLength() {
      int maxLength = validator.getMaxInputLength();
      assertThat(maxLength).isEqualTo(1000);
    }

    @Test
    @DisplayName("Should return correct min input length")
    void shouldReturnCorrectMinInputLength() {
      int minLength = validator.getMinInputLength();
      assertThat(minLength).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("Algorithm Validation Tests")
  class AlgorithmValidationTests {

    @Test
    @DisplayName("Should validate secure algorithm successfully")
    void shouldValidateSecureAlgorithmSuccessfully() {
      String algorithm = "SHA-256";
      lenient().when(cryptographicProvider.isAlgorithmSecure(algorithm)).thenReturn(true);

      ValidationResult result = validator.validateAlgorithm(algorithm);

      assertThat(result.isValid()).isTrue();
      assertThat(result.getSanitizedData()).isEqualTo(algorithm);
    }

    @Test
    @DisplayName("Should reject null algorithm")
    void shouldRejectNullAlgorithm() {
      ValidationResult result = validator.validateAlgorithm(null);

      assertThat(result.isValid()).isFalse();
      assertThat(result.getErrors()).contains("Algorithm name cannot be null or empty");
    }

    @Test
    @DisplayName("Should reject deprecated algorithm MD5")
    void shouldRejectDeprecatedAlgorithmMD5() {
      String algorithm = "MD5";

      ValidationResult result = validator.validateAlgorithm(algorithm);

      assertThat(result.isValid()).isFalse();
      assertThat(result.getErrors()).anyMatch(e -> e.contains("deprecated and insecure"));
    }

    @Test
    @DisplayName("Should reject algorithm with invalid characters")
    void shouldRejectAlgorithmWithInvalidCharacters() {
      String algorithm = "SHA-256!@#";

      ValidationResult result = validator.validateAlgorithm(algorithm);

      assertThat(result.isValid()).isFalse();
      assertThat(result.getErrors()).anyMatch(e -> e.contains("invalid characters"));
    }
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should throw exception when cryptographic provider is null")
    void shouldThrowExceptionWhenCryptographicProviderIsNull() {
      try {
        new SecurityInputValidator(null);
        assertThat(false).as("Expected NullPointerException").isTrue();
      } catch (NullPointerException e) {
        assertThat(e.getMessage()).contains("Cryptographic provider cannot be null");
      }
    }
  }
}
