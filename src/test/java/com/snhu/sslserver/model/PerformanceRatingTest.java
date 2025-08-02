package com.snhu.sslserver.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for PerformanceRating enum. Tests enum values, descriptions, and behavior. */
@DisplayName("PerformanceRating Tests")
class PerformanceRatingTest {

  @Test
  @DisplayName("Should have correct enum values")
  void shouldHaveCorrectEnumValues() {
    // When
    PerformanceRating[] values = PerformanceRating.values();

    // Then
    assertEquals(3, values.length);
    assertEquals(PerformanceRating.FAST, values[0]);
    assertEquals(PerformanceRating.MEDIUM, values[1]);
    assertEquals(PerformanceRating.SLOW, values[2]);
  }

  @Test
  @DisplayName("Should have correct descriptions for each rating")
  void shouldHaveCorrectDescriptionsForEachRating() {
    // Then
    assertEquals(
        "Fast - Excellent performance for high-throughput scenarios",
        PerformanceRating.FAST.getDescription());
    assertEquals(
        "Medium - Good balance of performance and security",
        PerformanceRating.MEDIUM.getDescription());
    assertEquals(
        "Slow - Maximum security, suitable for high-security scenarios",
        PerformanceRating.SLOW.getDescription());
  }

  @Test
  @DisplayName("Should support valueOf operations")
  void shouldSupportValueOfOperations() {
    // When & Then
    assertEquals(PerformanceRating.FAST, PerformanceRating.valueOf("FAST"));
    assertEquals(PerformanceRating.MEDIUM, PerformanceRating.valueOf("MEDIUM"));
    assertEquals(PerformanceRating.SLOW, PerformanceRating.valueOf("SLOW"));
  }

  @Test
  @DisplayName("Should throw IllegalArgumentException for invalid valueOf")
  void shouldThrowExceptionForInvalidValueOf() {
    // When & Then
    assertThrows(IllegalArgumentException.class, () -> PerformanceRating.valueOf("INVALID"));
  }

  @Test
  @DisplayName("Should have proper toString representation")
  void shouldHaveProperToStringRepresentation() {
    // Then
    assertEquals("FAST", PerformanceRating.FAST.toString());
    assertEquals("MEDIUM", PerformanceRating.MEDIUM.toString());
    assertEquals("SLOW", PerformanceRating.SLOW.toString());
  }

  @Test
  @DisplayName("Should support ordinal operations")
  void shouldSupportOrdinalOperations() {
    // Then
    assertEquals(0, PerformanceRating.FAST.ordinal());
    assertEquals(1, PerformanceRating.MEDIUM.ordinal());
    assertEquals(2, PerformanceRating.SLOW.ordinal());
  }

  @Test
  @DisplayName("Should support comparison operations")
  void shouldSupportComparisonOperations() {
    // Then
    assertTrue(PerformanceRating.FAST.compareTo(PerformanceRating.MEDIUM) < 0);
    assertTrue(PerformanceRating.MEDIUM.compareTo(PerformanceRating.SLOW) < 0);
    assertTrue(PerformanceRating.SLOW.compareTo(PerformanceRating.FAST) > 0);
    assertEquals(0, PerformanceRating.FAST.compareTo(PerformanceRating.FAST));
  }

  @Test
  @DisplayName("Should be usable in switch statements")
  void shouldBeUsableInSwitchStatements() {
    // Given & When
    String result = getPerformanceCategory(PerformanceRating.FAST);

    // Then
    assertEquals("High Performance", result);
  }

  @Test
  @DisplayName("Should handle all enum values in switch")
  void shouldHandleAllEnumValuesInSwitch() {
    // Then
    assertEquals("High Performance", getPerformanceCategory(PerformanceRating.FAST));
    assertEquals("Balanced", getPerformanceCategory(PerformanceRating.MEDIUM));
    assertEquals("Security Focused", getPerformanceCategory(PerformanceRating.SLOW));
  }

  @Test
  @DisplayName("Should maintain enum identity")
  void shouldMaintainEnumIdentity() {
    // Given
    PerformanceRating rating1 = PerformanceRating.FAST;
    PerformanceRating rating2 = PerformanceRating.valueOf("FAST");

    // Then
    assertSame(rating1, rating2);
    assertEquals(rating1, rating2);
    assertEquals(rating1.hashCode(), rating2.hashCode());
  }

  @Test
  @DisplayName("Should be serializable")
  void shouldBeSerializable() {
    // Given
    PerformanceRating rating = PerformanceRating.MEDIUM;

    // Then - enum implements Serializable by default
    assertTrue(rating instanceof java.io.Serializable);
  }

  /** Helper method to test switch statement usage */
  private String getPerformanceCategory(PerformanceRating rating) {
    switch (rating) {
      case FAST:
        return "High Performance";
      case MEDIUM:
        return "Balanced";
      case SLOW:
        return "Security Focused";
      default:
        return "Unknown";
    }
  }
}
