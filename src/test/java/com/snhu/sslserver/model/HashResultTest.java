package com.snhu.sslserver.model;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for HashResult model class.
 * Tests constructor validation, getters, equals/hashCode, and builder pattern.
 */
@DisplayName("HashResult Tests")
class HashResultTest {

    private static final String SAMPLE_DATA = "Hello John Doe!";
    private static final String SAMPLE_ALGORITHM = "SHA-256";
    private static final String SAMPLE_HEX_HASH = "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3";
    private static final Instant SAMPLE_TIMESTAMP = Instant.now();
    private static final long SAMPLE_COMPUTATION_TIME = 15L;

    @Test
    @DisplayName("Should create HashResult with valid parameters")
    void shouldCreateHashResultWithValidParameters() {
        // When
        HashResult result = new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH,
                SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME);

        // Then
        assertEquals(SAMPLE_DATA, result.getOriginalData());
        assertEquals(SAMPLE_ALGORITHM, result.getAlgorithm());
        assertEquals(SAMPLE_HEX_HASH, result.getHexHash());
        assertEquals(SAMPLE_TIMESTAMP, result.getTimestamp());
        assertEquals(SAMPLE_COMPUTATION_TIME, result.getComputationTimeMs());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when originalData is null")
    void shouldThrowExceptionWhenOriginalDataIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new HashResult(null,
                SAMPLE_ALGORITHM, SAMPLE_HEX_HASH, SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME));
        assertEquals("Original data cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when algorithm is null")
    void shouldThrowExceptionWhenAlgorithmIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new HashResult(SAMPLE_DATA, null, SAMPLE_HEX_HASH, SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME));
        assertEquals("Algorithm cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when hexHash is null")
    void shouldThrowExceptionWhenHexHashIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, null, SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME));
        assertEquals("Hex hash cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when timestamp is null")
    void shouldThrowExceptionWhenTimestampIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH, null, SAMPLE_COMPUTATION_TIME));
        assertEquals("Timestamp cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when computationTimeMs is negative")
    void shouldThrowExceptionWhenComputationTimeIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH, SAMPLE_TIMESTAMP, -1L));
        assertEquals("Computation time cannot be negative: -1", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept zero computation time")
    void shouldAcceptZeroComputationTime() {
        // When
        HashResult result = new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH,
                SAMPLE_TIMESTAMP, 0L);

        // Then
        assertEquals(0L, result.getComputationTimeMs());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void shouldImplementEqualsAndHashCodeCorrectly() {
        // Given
        HashResult result1 = new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH,
                SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME);
        HashResult result2 = new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH,
                SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME);
        HashResult result3 = new HashResult("Different Data", SAMPLE_ALGORITHM, SAMPLE_HEX_HASH,
                SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME);

        // Then
        assertEquals(result1, result2);
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1, result3);
        assertNotEquals(result1.hashCode(), result3.hashCode());

        // Test reflexivity
        assertEquals(result1, result1);

        // Test null comparison
        assertNotEquals(result1, null);

        // Test different class comparison
        assertNotEquals(result1, "not a HashResult");
    }

    @Test
    @DisplayName("Should create HashResult using builder pattern")
    void shouldCreateHashResultUsingBuilder() {
        // When
        HashResult result = HashResult.builder()
                .originalData(SAMPLE_DATA)
                .algorithm(SAMPLE_ALGORITHM)
                .hexHash(SAMPLE_HEX_HASH)
                .timestamp(SAMPLE_TIMESTAMP)
                .computationTimeMs(SAMPLE_COMPUTATION_TIME)
                .build();

        // Then
        assertEquals(SAMPLE_DATA, result.getOriginalData());
        assertEquals(SAMPLE_ALGORITHM, result.getAlgorithm());
        assertEquals(SAMPLE_HEX_HASH, result.getHexHash());
        assertEquals(SAMPLE_TIMESTAMP, result.getTimestamp());
        assertEquals(SAMPLE_COMPUTATION_TIME, result.getComputationTimeMs());
    }

    @Test
    @DisplayName("Should throw exception in builder when computationTimeMs is negative")
    void shouldThrowExceptionInBuilderWhenComputationTimeIsNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> HashResult.builder()
                .originalData(SAMPLE_DATA)
                .algorithm(SAMPLE_ALGORITHM)
                .hexHash(SAMPLE_HEX_HASH)
                .timestamp(SAMPLE_TIMESTAMP)
                .computationTimeMs(-5L)
                .build());
        assertEquals("Computation time cannot be negative: -5", exception.getMessage());
    }

    @Test
    @DisplayName("Should have meaningful toString representation")
    void shouldHaveMeaningfulToStringRepresentation() {
        // Given
        HashResult result = new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH,
                SAMPLE_TIMESTAMP, SAMPLE_COMPUTATION_TIME);

        // When
        String toString = result.toString();

        // Then
        assertTrue(toString.contains("HashResult"));
        assertTrue(toString.contains(SAMPLE_DATA));
        assertTrue(toString.contains(SAMPLE_ALGORITHM));
        assertTrue(toString.contains(SAMPLE_HEX_HASH));
        assertTrue(toString.contains(String.valueOf(SAMPLE_COMPUTATION_TIME)));
    }

    @Test
    @DisplayName("Should handle edge case with very long computation time")
    void shouldHandleVeryLongComputationTime() {
        // Given
        long veryLongTime = Long.MAX_VALUE;

        // When
        HashResult result = new HashResult(SAMPLE_DATA, SAMPLE_ALGORITHM, SAMPLE_HEX_HASH,
                SAMPLE_TIMESTAMP, veryLongTime);

        // Then
        assertEquals(veryLongTime, result.getComputationTimeMs());
    }

    @Test
    @DisplayName("Should handle edge case with empty strings")
    void shouldHandleEmptyStrings() {
        // When
        HashResult result = new HashResult("", "", "", SAMPLE_TIMESTAMP, 0L);

        // Then
        assertEquals("", result.getOriginalData());
        assertEquals("", result.getAlgorithm());
        assertEquals("", result.getHexHash());
    }
}