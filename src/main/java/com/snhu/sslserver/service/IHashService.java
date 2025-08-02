package com.snhu.sslserver.service;

import java.util.List;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.model.AlgorithmInfo;
import com.snhu.sslserver.model.HashResult;

/**
 * Service interface for cryptographic hash operations.
 * This interface defines the contract for secure hash computation,
 * algorithm validation, and metadata retrieval.
 * 
 * Implementations must ensure:
 * - Only secure, collision-resistant algorithms are used
 * - Input validation and sanitization is performed
 * - Proper error handling without information leakage
 * - Thread-safe operations for concurrent access
 * 
 * @author Rick Goshen
 * @version 1.0
 */
public interface IHashService {
    
    /**
     * Computes cryptographic hash for given input using specified algorithm.
     * The input data should include student name and any additional data.
     * Only secure algorithms (SHA-256, SHA-3-256, SHA-512, SHA-3-512) are supported.
     * 
     * @param input The data to hash (student name + additional data)
     * @param algorithm The hash algorithm to use (SHA-256, SHA-3-256, etc.)
     * @return HashResult containing original data, algorithm, and hex hash
     * @throws CryptographicException if hash computation fails, algorithm is insecure, or input is invalid
     */
    HashResult computeHash(String input, String algorithm) throws CryptographicException;
    
    /**
     * Computes cryptographic hash using the default secure algorithm (SHA-256).
     * This is a convenience method for cases where algorithm selection is not specified.
     * 
     * @param input The data to hash (student name + additional data)
     * @return HashResult containing original data, algorithm, and hex hash
     * @throws CryptographicException if hash computation fails or input is invalid
     */
    HashResult computeHash(String input) throws CryptographicException;
    
    /**
     * Gets information about all supported cryptographic algorithms.
     * This includes algorithm names, security status, performance characteristics,
     * and descriptions suitable for user selection.
     * 
     * @return List of supported algorithm information, ordered by recommendation
     */
    List<AlgorithmInfo> getSupportedAlgorithms();
    
    /**
     * Validates if an algorithm is secure and supported by the system.
     * This method checks against the list of approved secure algorithms
     * and explicitly rejects deprecated algorithms like MD5 and SHA-1.
     * 
     * @param algorithm Algorithm name to validate (case-insensitive)
     * @return true if algorithm is secure and supported, false otherwise
     */
    boolean isAlgorithmSecure(String algorithm);
    
    /**
     * Gets the default algorithm used when no specific algorithm is requested.
     * The default algorithm balances security and performance for general use.
     * 
     * @return The name of the default algorithm (typically "SHA-256")
     */
    String getDefaultAlgorithm();
}