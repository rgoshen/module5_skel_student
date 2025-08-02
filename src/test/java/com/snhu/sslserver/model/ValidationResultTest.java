package com.snhu.sslserver.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ValidationResult model class. Tests static factory methods, immutability, and edge
 * cases.
 */
@DisplayName("ValidationResult Tests")
class ValidationResultTest {

  private static final String SAMPLE_DATA = "Hello World!";
  private static final String SAMPLE_ERROR = "Invalid input";
  private static final String SAMPLE_WARNING = "Input was modified";

  @Test
  @DisplayName("Should create successful validation result")
  void shouldCreateSuccessfulValidationResult() {
    // When
    ValidationResult result = ValidationResult.success(SAMPLE_DATA);

    // Then
    assertTrue(result.isValid());
    assertEquals(SAMPLE_DATA, result.getSanitizedData());
    assertTrue(result.getErrors().isEmpty());
    assertTrue(result.getWarnings().isEmpty());
    assertFalse(result.hasErrors());
    assertFalse(result.hasWarnings());
  }

  @Test
  @DisplayName("Should create successful validation result with warnings")
  void shouldCreateSuccessfulValidationResultWithWarnings() {
    // Given
    List<String> warnings = Arrays.asList(SAMPLE_WARNING, "Another warning");

    // When
    ValidationResult result = ValidationResult.successWithWarnings(SAMPLE_DATA, warnings);

    // Then
    assertTrue(result.isValid());
    assertEquals(SAMPLE_DATA, result.getSanitizedData());
    assertTrue(result.getErrors().isEmpty());
    assertEquals(2, result.getWarnings().size());
    assertTrue(result.getWarnings().contains(SAMPLE_WARNING));
    assertTrue(result.getWarnings().contains("Another warning"));
    assertFalse(result.hasErrors());
    assertTrue(result.hasWarnings());
  }

  @Test
  @DisplayName("Should create failed validation result with multiple errors")
  void shouldCreateFailedValidationResultWithMultipleErrors() {
    // Given
    List<String> errors = Arrays.asList(SAMPLE_ERROR, "Another error");

    // When
    ValidationResult result = ValidationResult.failure(errors);

    // Then
    assertFalse(result.isValid());
    assertNull(result.getSanitizedData());
    assertEquals(2, result.getErrors().size());
    assertTrue(result.getErrors().contains(SAMPLE_ERROR));
    assertTrue(result.getErrors().contains("Another error"));
    assertTrue(result.getWarnings().isEmpty());
    assertTrue(result.hasErrors());
    assertFalse(result.hasWarnings());
  }

  @Test
  @DisplayName("Should create failed validation result with single error")
  void shouldCreateFailedValidationResultWithSingleError() {
    // When
    ValidationResult result = ValidationResult.failure(SAMPLE_ERROR);

    // Then
    assertFalse(result.isValid());
    assertNull(result.getSanitizedData());
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().contains(SAMPLE_ERROR));
    assertTrue(result.getWarnings().isEmpty());
    assertTrue(result.hasErrors());
    assertFalse(result.hasWarnings());
  }

  @Test
  @DisplayName("Should create failed validation result with errors and warnings")
  void shouldCreateFailedValidationResultWithErrorsAndWarnings() {
    // Given
    List<String> errors = Arrays.asList(SAMPLE_ERROR);
    List<String> warnings = Arrays.asList(SAMPLE_WARNING);

    // When
    ValidationResult result = ValidationResult.failure(errors, warnings);

    // Then
    assertFalse(result.isValid());
    assertNull(result.getSanitizedData());
    assertEquals(1, result.getErrors().size());
    assertEquals(1, result.getWarnings().size());
    assertTrue(result.getErrors().contains(SAMPLE_ERROR));
    assertTrue(result.getWarnings().contains(SAMPLE_WARNING));
    assertTrue(result.hasErrors());
    assertTrue(result.hasWarnings());
  }

  @Test
  @DisplayName("Should return immutable error list")
  void shouldReturnImmutableErrorList() {
    // Given
    ValidationResult result = ValidationResult.failure(SAMPLE_ERROR);

    // When & Then
    assertThrows(UnsupportedOperationException.class, () -> result.getErrors().add("New error"));
  }

  @Test
  @DisplayName("Should return immutable warning list")
  void shouldReturnImmutableWarningList() {
    // Given
    List<String> warnings = Arrays.asList(SAMPLE_WARNING);
    ValidationResult result = ValidationResult.successWithWarnings(SAMPLE_DATA, warnings);

    // When & Then
    assertThrows(
        UnsupportedOperationException.class, () -> result.getWarnings().add("New warning"));
  }

  @Test
  @DisplayName("Should not be affected by changes to original error list")
  void shouldNotBeAffectedByChangesToOriginalErrorList() {
    // Given
    List<String> originalErrors = new ArrayList<>();
    originalErrors.add(SAMPLE_ERROR);

    ValidationResult result = ValidationResult.failure(originalErrors);

    // When
    originalErrors.add("New error");

    // Then
    assertEquals(1, result.getErrors().size());
    assertTrue(result.getErrors().contains(SAMPLE_ERROR));
    assertFalse(result.getErrors().contains("New error"));
  }

  @Test
  @DisplayName("Should not be affected by changes to original warning list")
  void shouldNotBeAffectedByChangesToOriginalWarningList() {
    // Given
    List<String> originalWarnings = new ArrayList<>();
    originalWarnings.add(SAMPLE_WARNING);

    ValidationResult result = ValidationResult.successWithWarnings(SAMPLE_DATA, originalWarnings);

    // When
    originalWarnings.add("New warning");

    // Then
    assertEquals(1, result.getWarnings().size());
    assertTrue(result.getWarnings().contains(SAMPLE_WARNING));
    assertFalse(result.getWarnings().contains("New warning"));
  }

  @Test
  @DisplayName("Should implement equals and hashCode correctly")
  void shouldImplementEqualsAndHashCodeCorrectly() {
    // Given
    ValidationResult result1 = ValidationResult.success(SAMPLE_DATA);
    ValidationResult result2 = ValidationResult.success(SAMPLE_DATA);
    ValidationResult result3 = ValidationResult.success("Different data");
    ValidationResult result4 = ValidationResult.failure(SAMPLE_ERROR);

    // Then
    assertEquals(result1, result2);
    assertEquals(result1.hashCode(), result2.hashCode());
    assertNotEquals(result1, result3);
    assertNotEquals(result1, result4);

    // Test reflexivity
    assertEquals(result1, result1);

    // Test null comparison
    assertNotEquals(result1, null);

    // Test different class comparison
    assertNotEquals(result1, "not a ValidationResult");
  }

  @Test
  @DisplayName("Should have meaningful toString representation")
  void shouldHaveMeaningfulToStringRepresentation() {
    // Given
    ValidationResult result = ValidationResult.success(SAMPLE_DATA);

    // When
    String toString = result.toString();

    // Then
    assertTrue(toString.contains("ValidationResult"));
    assertTrue(toString.contains("valid=true"));
    assertTrue(toString.contains(SAMPLE_DATA));
  }

  @Test
  @DisplayName("Should handle empty error and warning lists")
  void shouldHandleEmptyErrorAndWarningLists() {
    // Given
    List<String> emptyErrors = Collections.emptyList();
    List<String> emptyWarnings = Collections.emptyList();

    // When
    ValidationResult result = ValidationResult.failure(emptyErrors, emptyWarnings);

    // Then
    assertFalse(result.isValid());
    assertTrue(result.getErrors().isEmpty());
    assertTrue(result.getWarnings().isEmpty());
    assertFalse(result.hasErrors());
    assertFalse(result.hasWarnings());
  }

  @Test
  @DisplayName("Should handle null sanitized data in success case")
  void shouldHandleNullSanitizedDataInSuccessCase() {
    // When
    ValidationResult result = ValidationResult.success(null);

    // Then
    assertTrue(result.isValid());
    assertNull(result.getSanitizedData());
  }

  @Test
  @DisplayName("Should handle empty string sanitized data")
  void shouldHandleEmptyStringSanitizedData() {
    // When
    ValidationResult result = ValidationResult.success("");

    // Then
    assertTrue(result.isValid());
    assertEquals("", result.getSanitizedData());
  }

  @Test
  @DisplayName("Should handle very long error messages")
  void shouldHandleVeryLongErrorMessages() {
    // Given
    String longError =
        "This is a very long error message that contains many characters and should be handled properly by the ValidationResult class without any issues or truncation";

    // When
    ValidationResult result = ValidationResult.failure(longError);

    // Then
    assertFalse(result.isValid());
    assertEquals(1, result.getErrors().size());
    assertEquals(longError, result.getErrors().get(0));
  }

  @Test
  @DisplayName("Should handle special characters in messages")
  void shouldHandleSpecialCharactersInMessages() {
    // Given
    String specialError = "Error with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
    String specialWarning = "Warning with unicode: αβγδε 中文 🚀";

    // When
    ValidationResult result =
        ValidationResult.failure(Arrays.asList(specialError), Arrays.asList(specialWarning));

    // Then
    assertTrue(result.getErrors().contains(specialError));
    assertTrue(result.getWarnings().contains(specialWarning));
  }

  @Test
  @DisplayName("Should maintain order of errors and warnings")
  void shouldMaintainOrderOfErrorsAndWarnings() {
    // Given
    List<String> errors = Arrays.asList("Error 1", "Error 2", "Error 3");
    List<String> warnings = Arrays.asList("Warning 1", "Warning 2");

    // When
    ValidationResult result = ValidationResult.failure(errors, warnings);

    // Then
    assertEquals("Error 1", result.getErrors().get(0));
    assertEquals("Error 2", result.getErrors().get(1));
    assertEquals("Error 3", result.getErrors().get(2));
    assertEquals("Warning 1", result.getWarnings().get(0));
    assertEquals("Warning 2", result.getWarnings().get(1));
  }
}
