package com.snhu.sslserver.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for AlgorithmInfo model class. Tests constructor validation, getters, equals/hashCode,
 * builder pattern, and immutable set handling.
 */
@DisplayName("AlgorithmInfo Tests")
class AlgorithmInfoTest {

  private static final String SAMPLE_NAME = "SHA-256";
  private static final boolean SAMPLE_SECURE = true;
  private static final PerformanceRating SAMPLE_PERFORMANCE = PerformanceRating.FAST;
  private static final String SAMPLE_DESCRIPTION = "Secure Hash Algorithm 256-bit";
  private static final Set<String> SAMPLE_ALIASES = createTestAliases();

  private static Set<String> createTestAliases() {
    Set<String> aliases = new HashSet<>();
    aliases.add("SHA256");
    aliases.add("SHA-2-256");
    return aliases;
  }

  @Test
  @DisplayName("Should create AlgorithmInfo with valid parameters")
  void shouldCreateAlgorithmInfoWithValidParameters() {
    // When
    AlgorithmInfo info =
        new AlgorithmInfo(
            SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, SAMPLE_ALIASES);

    // Then
    assertEquals(SAMPLE_NAME, info.getName());
    assertEquals(SAMPLE_SECURE, info.isSecure());
    assertEquals(SAMPLE_PERFORMANCE, info.getPerformance());
    assertEquals(SAMPLE_DESCRIPTION, info.getDescription());
    assertEquals(SAMPLE_ALIASES, info.getAliases());
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when name is null")
  void shouldThrowExceptionWhenNameIsNull() {
    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new AlgorithmInfo(
                    null, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, SAMPLE_ALIASES));
    assertEquals("Algorithm name cannot be null", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when performance is null")
  void shouldThrowExceptionWhenPerformanceIsNull() {
    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new AlgorithmInfo(
                    SAMPLE_NAME, SAMPLE_SECURE, null, SAMPLE_DESCRIPTION, SAMPLE_ALIASES));
    assertEquals("Performance rating cannot be null", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when description is null")
  void shouldThrowExceptionWhenDescriptionIsNull() {
    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new AlgorithmInfo(
                    SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, null, SAMPLE_ALIASES));
    assertEquals("Description cannot be null", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException when aliases is null")
  void shouldThrowExceptionWhenAliasesIsNull() {
    // When & Then
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new AlgorithmInfo(
                    SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, null));
    assertEquals("Aliases cannot be null", exception.getMessage());
  }

  @Test
  @DisplayName("Should handle empty aliases set")
  void shouldHandleEmptyAliasesSet() {
    // Given
    Set<String> emptyAliases = Collections.emptySet();

    // When
    AlgorithmInfo info =
        new AlgorithmInfo(
            SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, emptyAliases);

    // Then
    assertTrue(info.getAliases().isEmpty());
  }

  @Test
  @DisplayName("Should create immutable aliases set")
  void shouldCreateImmutableAliasesSet() {
    // Given
    Set<String> mutableAliases = new HashSet<>();
    mutableAliases.add("SHA256");

    // When
    AlgorithmInfo info =
        new AlgorithmInfo(
            SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, mutableAliases);

    // Then
    assertThrows(UnsupportedOperationException.class, () -> info.getAliases().add("NewAlias"));
  }

  @Test
  @DisplayName("Should not be affected by changes to original aliases set")
  void shouldNotBeAffectedByChangesToOriginalAliasesSet() {
    // Given
    Set<String> originalAliases = new HashSet<>();
    originalAliases.add("SHA256");

    AlgorithmInfo info =
        new AlgorithmInfo(
            SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, originalAliases);

    // When
    originalAliases.add("NewAlias");

    // Then
    assertEquals(1, info.getAliases().size());
    assertTrue(info.getAliases().contains("SHA256"));
    assertFalse(info.getAliases().contains("NewAlias"));
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    AlgorithmInfo info1 =
        new AlgorithmInfo(
            SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, SAMPLE_ALIASES);
    AlgorithmInfo info2 =
        new AlgorithmInfo(
            SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, SAMPLE_ALIASES);
    AlgorithmInfo info3 =
        new AlgorithmInfo(
            "Different", SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, SAMPLE_ALIASES);

    // Then
    assertEquals(info1, info2);
    assertEquals(info1.hashCode(), info2.hashCode());
    assertNotEquals(info1, info3);
    assertNotEquals(info1.hashCode(), info3.hashCode());

    // Test reflexivity
    assertEquals(info1, info1);

    // Test null comparison
    assertNotEquals(info1, null);

    // Test different class comparison
    assertNotEquals(info1, "not an AlgorithmInfo");
  }

  @Test
  @DisplayName("Should create AlgorithmInfo using builder pattern")
  void shouldCreateAlgorithmInfoUsingBuilder() {
    // When
    AlgorithmInfo info =
        AlgorithmInfo.builder()
            .name(SAMPLE_NAME)
            .secure(SAMPLE_SECURE)
            .performance(SAMPLE_PERFORMANCE)
            .description(SAMPLE_DESCRIPTION)
            .aliases(SAMPLE_ALIASES)
            .build();

    // Then
    assertEquals(SAMPLE_NAME, info.getName());
    assertEquals(SAMPLE_SECURE, info.isSecure());
    assertEquals(SAMPLE_PERFORMANCE, info.getPerformance());
    assertEquals(SAMPLE_DESCRIPTION, info.getDescription());
    assertEquals(SAMPLE_ALIASES, info.getAliases());
  }

  @Test
  @DisplayName("Should use empty set as default aliases in builder")
  void shouldUseEmptySetAsDefaultAliasesInBuilder() {
    // When
    AlgorithmInfo info =
        AlgorithmInfo.builder()
            .name(SAMPLE_NAME)
            .secure(SAMPLE_SECURE)
            .performance(SAMPLE_PERFORMANCE)
            .description(SAMPLE_DESCRIPTION)
            .build();

    // Then
    assertTrue(info.getAliases().isEmpty());
  }

  @Test
  @DisplayName("Should have meaningful toString representation")
  void shouldHaveMeaningfulToStringRepresentation() {
    // Given
    AlgorithmInfo info =
        new AlgorithmInfo(
            SAMPLE_NAME, SAMPLE_SECURE, SAMPLE_PERFORMANCE, SAMPLE_DESCRIPTION, SAMPLE_ALIASES);

    // When
    String toString = info.toString();

    // Then
    assertTrue(toString.contains("AlgorithmInfo"));
    assertTrue(toString.contains(SAMPLE_NAME));
    assertTrue(toString.contains(String.valueOf(SAMPLE_SECURE)));
    assertTrue(toString.contains(SAMPLE_PERFORMANCE.toString()));
    assertTrue(toString.contains(SAMPLE_DESCRIPTION));
  }

  @Test
  @DisplayName("Should handle insecure algorithm")
  void shouldHandleInsecureAlgorithm() {
    // When
    AlgorithmInfo info =
        new AlgorithmInfo(
            "MD5",
            false,
            PerformanceRating.FAST,
            "Deprecated hash algorithm",
            Collections.emptySet());

    // Then
    assertEquals("MD5", info.getName());
    assertFalse(info.isSecure());
    assertEquals(PerformanceRating.FAST, info.getPerformance());
  }

  @Test
  @DisplayName("Should handle all performance ratings")
  void shouldHandleAllPerformanceRatings() {
    // Test FAST
    AlgorithmInfo fastInfo =
        new AlgorithmInfo(
            "SHA-256", true, PerformanceRating.FAST, "Fast algorithm", Collections.emptySet());
    assertEquals(PerformanceRating.FAST, fastInfo.getPerformance());

    // Test MEDIUM
    AlgorithmInfo mediumInfo =
        new AlgorithmInfo(
            "SHA-384", true, PerformanceRating.MEDIUM, "Medium algorithm", Collections.emptySet());
    assertEquals(PerformanceRating.MEDIUM, mediumInfo.getPerformance());

    // Test SLOW
    AlgorithmInfo slowInfo =
        new AlgorithmInfo(
            "SHA-512", true, PerformanceRating.SLOW, "Slow algorithm", Collections.emptySet());
    assertEquals(PerformanceRating.SLOW, slowInfo.getPerformance());
  }

  @Test
  @DisplayName("Should handle edge case with empty strings")
  void shouldHandleEmptyStrings() {
    // When
    AlgorithmInfo info =
        new AlgorithmInfo("", false, PerformanceRating.MEDIUM, "", Collections.emptySet());

    // Then
    assertEquals("", info.getName());
    assertEquals("", info.getDescription());
    assertFalse(info.isSecure());
  }
}
