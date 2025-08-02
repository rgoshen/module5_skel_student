package com.snhu.sslserver.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.snhu.sslserver.strategy.HashAlgorithmStrategy;

/**
 * Unit tests for HashAlgorithmFactory. Tests cover strategy creation, lookup, and management
 * functionality.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class HashAlgorithmFactoryTest {

  @Mock private HashAlgorithmStrategy sha256Strategy;

  @Mock private HashAlgorithmStrategy sha3Strategy;

  @Mock private HashAlgorithmStrategy sha512Strategy;

  private HashAlgorithmFactory factory;

  @BeforeEach
  void setUp() {
    // Set up mock strategies
    when(sha256Strategy.getAlgorithmName()).thenReturn("SHA-256");
    when(sha3Strategy.getAlgorithmName()).thenReturn("SHA-3-256");
    when(sha512Strategy.getAlgorithmName()).thenReturn("SHA-512");

    List<HashAlgorithmStrategy> strategies =
        Arrays.asList(sha256Strategy, sha3Strategy, sha512Strategy);
    factory = new HashAlgorithmFactory(strategies);
  }

  @Test
  @DisplayName("Should create strategy for supported algorithm")
  void shouldCreateStrategyForSupportedAlgorithm() {
    // Act
    HashAlgorithmStrategy result = factory.createStrategy("SHA-256");

    // Assert
    assertThat(result).isEqualTo(sha256Strategy);
  }

  @Test
  @DisplayName("Should create strategy with case-insensitive algorithm name")
  void shouldCreateStrategyWithCaseInsensitiveAlgorithmName() {
    // Act
    HashAlgorithmStrategy result1 = factory.createStrategy("sha-256");
    HashAlgorithmStrategy result2 = factory.createStrategy("SHA-256");
    HashAlgorithmStrategy result3 = factory.createStrategy("Sha-256");

    // Assert
    assertThat(result1).isEqualTo(sha256Strategy);
    assertThat(result2).isEqualTo(sha256Strategy);
    assertThat(result3).isEqualTo(sha256Strategy);
  }

  @Test
  @DisplayName("Should handle whitespace in algorithm names")
  void shouldHandleWhitespaceInAlgorithmNames() {
    // Act
    HashAlgorithmStrategy result = factory.createStrategy("  SHA-256  ");

    // Assert
    assertThat(result).isEqualTo(sha256Strategy);
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException for unsupported algorithm")
  void shouldThrowExceptionForUnsupportedAlgorithm() {
    // Act & Assert
    assertThatThrownBy(() -> factory.createStrategy("MD5"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported algorithm: MD5")
        .hasMessageContaining("Supported algorithms:");
  }

  @Test
  @DisplayName("Should throw NullPointerException for null algorithm name")
  void shouldThrowExceptionForNullAlgorithmName() {
    // Act & Assert
    assertThatThrownBy(() -> factory.createStrategy(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Algorithm name cannot be null");
  }

  @Test
  @DisplayName("Should return Optional with strategy for supported algorithm")
  void shouldReturnOptionalWithStrategyForSupportedAlgorithm() {
    // Act
    Optional<HashAlgorithmStrategy> result = factory.getStrategy("SHA-3-256");

    // Assert
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(sha3Strategy);
  }

  @Test
  @DisplayName("Should return empty Optional for unsupported algorithm")
  void shouldReturnEmptyOptionalForUnsupportedAlgorithm() {
    // Act
    Optional<HashAlgorithmStrategy> result = factory.getStrategy("MD5");

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return empty Optional for null algorithm name")
  void shouldReturnEmptyOptionalForNullAlgorithmName() {
    // Act
    Optional<HashAlgorithmStrategy> result = factory.getStrategy(null);

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should correctly identify supported algorithms")
  void shouldCorrectlyIdentifySupportedAlgorithms() {
    // Act & Assert
    assertThat(factory.isAlgorithmSupported("SHA-256")).isTrue();
    assertThat(factory.isAlgorithmSupported("SHA-3-256")).isTrue();
    assertThat(factory.isAlgorithmSupported("SHA-512")).isTrue();
    assertThat(factory.isAlgorithmSupported("MD5")).isFalse();
    assertThat(factory.isAlgorithmSupported("UNKNOWN")).isFalse();
    assertThat(factory.isAlgorithmSupported(null)).isFalse();
  }

  @Test
  @DisplayName("Should support case-insensitive algorithm checking")
  void shouldSupportCaseInsensitiveAlgorithmChecking() {
    // Act & Assert
    assertThat(factory.isAlgorithmSupported("sha-256")).isTrue();
    assertThat(factory.isAlgorithmSupported("SHA-256")).isTrue();
    assertThat(factory.isAlgorithmSupported("Sha-256")).isTrue();
  }

  @Test
  @DisplayName("Should return all available strategies")
  void shouldReturnAllAvailableStrategies() {
    // Act
    List<HashAlgorithmStrategy> strategies = factory.getAllStrategies();

    // Assert
    assertThat(strategies).hasSize(3);
    assertThat(strategies).containsExactlyInAnyOrder(sha256Strategy, sha3Strategy, sha512Strategy);
  }

  @Test
  @DisplayName("Should handle empty strategy list")
  void shouldHandleEmptyStrategyList() {
    // Arrange
    HashAlgorithmFactory emptyFactory = new HashAlgorithmFactory(Collections.emptyList());

    // Act & Assert
    assertThat(emptyFactory.getAllStrategies()).isEmpty();
    assertThat(emptyFactory.isAlgorithmSupported("SHA-256")).isFalse();
    assertThat(emptyFactory.getStrategy("SHA-256")).isEmpty();

    assertThatThrownBy(() -> emptyFactory.createStrategy("SHA-256"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Unsupported algorithm");
  }

  @Test
  @DisplayName("Should throw NullPointerException for null strategy list")
  void shouldThrowExceptionForNullStrategyList() {
    // Act & Assert
    assertThatThrownBy(() -> new HashAlgorithmFactory(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("Strategy list cannot be null");
  }

  @Test
  @DisplayName("Should handle duplicate algorithm names by using last strategy")
  void shouldHandleDuplicateAlgorithmNames() {
    // Arrange
    HashAlgorithmStrategy duplicateStrategy = org.mockito.Mockito.mock(HashAlgorithmStrategy.class);
    when(duplicateStrategy.getAlgorithmName()).thenReturn("SHA-256");

    List<HashAlgorithmStrategy> strategiesWithDuplicate =
        Arrays.asList(sha256Strategy, duplicateStrategy);
    HashAlgorithmFactory factoryWithDuplicate = new HashAlgorithmFactory(strategiesWithDuplicate);

    // Act
    HashAlgorithmStrategy result = factoryWithDuplicate.createStrategy("SHA-256");

    // Assert - Should use the last strategy with that name
    assertThat(result).isEqualTo(duplicateStrategy);
  }
}
