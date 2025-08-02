package com.snhu.sslserver.service;

import com.snhu.sslserver.model.ValidationResult;

/**
 * Interface for input validation and sanitization operations.
 * This interface provides comprehensive validation for hash input data
 * and algorithm names to prevent security vulnerabilities and ensure
 * data integrity.
 * 
 * Implementations must ensure:
 * - Comprehensive input sanitization
 * - Protection against injection attacks
 * - Proper encoding validation
 * - Algorithm security validation
 * 
 * @author Rick Goshen
 * @version 1.0
 */
public interface IInputValidator {
    
    /**
     * Validates and sanitizes input data for hash computation.
     * This method performs comprehensive validation including:
     * - Length validation (prevents excessive memory usage)
     * - Character encoding validation (ensures UTF-8 compatibility)
     * - Content sanitization (removes potentially harmful characters)
     * - Format validation (ensures proper data structure)
     * 
     * @param input Raw input data to validate and sanitize
     * @return ValidationResult containing sanitized data or error details
     */
    ValidationResult validateAndSanitize(String input);
    
    /**
     * Validates algorithm name for security and system support.
     * This method checks that the algorithm:
     * - Is supported by the system
     * - Is cryptographically secure (not deprecated)
     * - Has proper name format and casing
     * - Is not on the blocked/insecure algorithm list
     * 
     * @param algorithm Algorithm name to validate (case-insensitive)
     * @return ValidationResult indicating if algorithm is acceptable
     */
    ValidationResult validateAlgorithm(String algorithm);
    
    /**
     * Validates the length of input data against system limits.
     * This prevents denial-of-service attacks through excessive
     * memory usage and ensures reasonable processing times.
     * 
     * @param input Input data to check
     * @return ValidationResult indicating if length is acceptable
     */
    ValidationResult validateInputLength(String input);
    
    /**
     * Validates that input data contains only safe characters
     * and proper encoding. This helps prevent injection attacks
     * and ensures consistent hash computation.
     * 
     * @param input Input data to validate
     * @return ValidationResult indicating if content is safe
     */
    ValidationResult validateInputContent(String input);
    
    /**
     * Gets the maximum allowed input length for hash operations.
     * This limit balances security (preventing DoS) with usability.
     * 
     * @return Maximum input length in characters
     */
    int getMaxInputLength();
    
    /**
     * Gets the minimum required input length for hash operations.
     * This ensures meaningful data is provided for hashing.
     * 
     * @return Minimum input length in characters
     */
    int getMinInputLength();
}