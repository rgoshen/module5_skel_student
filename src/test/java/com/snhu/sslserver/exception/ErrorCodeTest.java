package com.snhu.sslserver.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ErrorCode enum.
 * Tests enum values, default messages, and behavior.
 */
@DisplayName("ErrorCode Tests")
class ErrorCodeTest {

    @Test
    @DisplayName("Should have correct enum values")
    void shouldHaveCorrectEnumValues() {
        // When
        ErrorCode[] values = ErrorCode.values();
        
        // Then
        assertEquals(5, values.length);
        assertEquals(ErrorCode.ALGORITHM_NOT_SUPPORTED, values[0]);
        assertEquals(ErrorCode.ALGORITHM_INSECURE, values[1]);
        assertEquals(ErrorCode.COMPUTATION_FAILED, values[2]);
        assertEquals(ErrorCode.INPUT_VALIDATION_FAILED, values[3]);
        assertEquals(ErrorCode.CONFIGURATION_ERROR, values[4]);
    }

    @Test
    @DisplayName("Should have appropriate default messages")
    void shouldHaveAppropriateDefaultMessages() {
        // Then
        assertEquals("The specified algorithm is not supported", 
                    ErrorCode.ALGORITHM_NOT_SUPPORTED.getDefaultMessage());
        assertEquals("The specified algorithm is not secure and cannot be used", 
                    ErrorCode.ALGORITHM_INSECURE.getDefaultMessage());
        assertEquals("Hash computation failed", 
                    ErrorCode.COMPUTATION_FAILED.getDefaultMessage());
        assertEquals("Input validation failed", 
                    ErrorCode.INPUT_VALIDATION_FAILED.getDefaultMessage());
        assertEquals("System configuration error", 
                    ErrorCode.CONFIGURATION_ERROR.getDefaultMessage());
    }

    @Test
    @DisplayName("Should support valueOf operations")
    void shouldSupportValueOfOperations() {
        // When & Then
        assertEquals(ErrorCode.ALGORITHM_NOT_SUPPORTED, ErrorCode.valueOf("ALGORITHM_NOT_SUPPORTED"));
        assertEquals(ErrorCode.ALGORITHM_INSECURE, ErrorCode.valueOf("ALGORITHM_INSECURE"));
        assertEquals(ErrorCode.COMPUTATION_FAILED, ErrorCode.valueOf("COMPUTATION_FAILED"));
        assertEquals(ErrorCode.INPUT_VALIDATION_FAILED, ErrorCode.valueOf("INPUT_VALIDATION_FAILED"));
        assertEquals(ErrorCode.CONFIGURATION_ERROR, ErrorCode.valueOf("CONFIGURATION_ERROR"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid valueOf")
    void shouldThrowExceptionForInvalidValueOf() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> 
            ErrorCode.valueOf("INVALID_ERROR_CODE")
        );
    }

    @Test
    @DisplayName("Should have proper toString representation")
    void shouldHaveProperToStringRepresentation() {
        // Then
        assertEquals("ALGORITHM_NOT_SUPPORTED", ErrorCode.ALGORITHM_NOT_SUPPORTED.toString());
        assertEquals("ALGORITHM_INSECURE", ErrorCode.ALGORITHM_INSECURE.toString());
        assertEquals("COMPUTATION_FAILED", ErrorCode.COMPUTATION_FAILED.toString());
        assertEquals("INPUT_VALIDATION_FAILED", ErrorCode.INPUT_VALIDATION_FAILED.toString());
        assertEquals("CONFIGURATION_ERROR", ErrorCode.CONFIGURATION_ERROR.toString());
    }

    @Test
    @DisplayName("Should support ordinal operations")
    void shouldSupportOrdinalOperations() {
        // Then
        assertEquals(0, ErrorCode.ALGORITHM_NOT_SUPPORTED.ordinal());
        assertEquals(1, ErrorCode.ALGORITHM_INSECURE.ordinal());
        assertEquals(2, ErrorCode.COMPUTATION_FAILED.ordinal());
        assertEquals(3, ErrorCode.INPUT_VALIDATION_FAILED.ordinal());
        assertEquals(4, ErrorCode.CONFIGURATION_ERROR.ordinal());
    }

    @Test
    @DisplayName("Should be usable in switch statements")
    void shouldBeUsableInSwitchStatements() {
        // Given & When
        String category = getErrorCategory(ErrorCode.ALGORITHM_NOT_SUPPORTED);
        
        // Then
        assertEquals("Algorithm Error", category);
    }

    @Test
    @DisplayName("Should handle all enum values in switch")
    void shouldHandleAllEnumValuesInSwitch() {
        // Then
        assertEquals("Algorithm Error", getErrorCategory(ErrorCode.ALGORITHM_NOT_SUPPORTED));
        assertEquals("Algorithm Error", getErrorCategory(ErrorCode.ALGORITHM_INSECURE));
        assertEquals("Computation Error", getErrorCategory(ErrorCode.COMPUTATION_FAILED));
        assertEquals("Input Error", getErrorCategory(ErrorCode.INPUT_VALIDATION_FAILED));
        assertEquals("System Error", getErrorCategory(ErrorCode.CONFIGURATION_ERROR));
    }

    @Test
    @DisplayName("Should maintain enum identity")
    void shouldMaintainEnumIdentity() {
        // Given
        ErrorCode code1 = ErrorCode.ALGORITHM_NOT_SUPPORTED;
        ErrorCode code2 = ErrorCode.valueOf("ALGORITHM_NOT_SUPPORTED");
        
        // Then
        assertSame(code1, code2);
        assertEquals(code1, code2);
        assertEquals(code1.hashCode(), code2.hashCode());
    }

    @Test
    @DisplayName("Should support comparison operations")
    void shouldSupportComparisonOperations() {
        // Then
        assertTrue(ErrorCode.ALGORITHM_NOT_SUPPORTED.compareTo(ErrorCode.ALGORITHM_INSECURE) < 0);
        assertTrue(ErrorCode.COMPUTATION_FAILED.compareTo(ErrorCode.CONFIGURATION_ERROR) < 0);
        assertEquals(0, ErrorCode.ALGORITHM_NOT_SUPPORTED.compareTo(ErrorCode.ALGORITHM_NOT_SUPPORTED));
    }

    @Test
    @DisplayName("Should be serializable")
    void shouldBeSerializable() {
        // Given
        ErrorCode errorCode = ErrorCode.COMPUTATION_FAILED;
        
        // Then - enum implements Serializable by default
        assertTrue(errorCode instanceof java.io.Serializable);
    }

    @Test
    @DisplayName("Should have meaningful default messages for user display")
    void shouldHaveMeaningfulDefaultMessagesForUserDisplay() {
        // Test that all default messages are user-friendly and don't expose internal details
        for (ErrorCode errorCode : ErrorCode.values()) {
            String message = errorCode.getDefaultMessage();
            
            // Then
            assertNotNull(message);
            assertFalse(message.isEmpty());
            assertFalse(message.toLowerCase().contains("exception"));
            assertFalse(message.toLowerCase().contains("stack"));
            assertFalse(message.toLowerCase().contains("internal"));
        }
    }

    @Test
    @DisplayName("Should categorize errors appropriately for security")
    void shouldCategorizeErrorsAppropriatelyForSecurity() {
        // Algorithm-related errors should be distinguishable
        assertTrue(ErrorCode.ALGORITHM_NOT_SUPPORTED.name().contains("ALGORITHM"));
        assertTrue(ErrorCode.ALGORITHM_INSECURE.name().contains("ALGORITHM"));
        
        // Different error types should be clearly separated
        assertNotEquals(ErrorCode.ALGORITHM_NOT_SUPPORTED, ErrorCode.COMPUTATION_FAILED);
        assertNotEquals(ErrorCode.INPUT_VALIDATION_FAILED, ErrorCode.CONFIGURATION_ERROR);
    }

    @Test
    @DisplayName("Should provide consistent naming convention")
    void shouldProvideConsistentNamingConvention() {
        // All error codes should follow UPPER_SNAKE_CASE convention
        for (ErrorCode errorCode : ErrorCode.values()) {
            String name = errorCode.name();
            
            // Then
            assertEquals(name.toUpperCase(), name);
            assertTrue(name.matches("^[A-Z_]+$"));
        }
    }

    /**
     * Helper method to test switch statement usage
     */
    private String getErrorCategory(ErrorCode errorCode) {
        switch (errorCode) {
            case ALGORITHM_NOT_SUPPORTED:
            case ALGORITHM_INSECURE:
                return "Algorithm Error";
            case COMPUTATION_FAILED:
                return "Computation Error";
            case INPUT_VALIDATION_FAILED:
                return "Input Error";
            case CONFIGURATION_ERROR:
                return "System Error";
            default:
                return "Unknown Error";
        }
    }
}