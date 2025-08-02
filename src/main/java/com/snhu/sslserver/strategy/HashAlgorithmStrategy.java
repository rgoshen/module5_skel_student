package com.snhu.sslserver.strategy;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.model.PerformanceRating;

/**
 * Strategy interface for hash algorithm implementations. This interface defines the contract for
 * different hash algorithm strategies, enabling the Strategy pattern for algorithm selection and
 * execution.
 *
 * <p>Implementations must ensure: - Thread-safe operations for concurrent access - Proper error
 * handling with CryptographicException - Consistent hash computation for identical inputs -
 * Security validation of algorithm implementation
 *
 * @author Rick Goshen
 * @version 1.0
 */
public interface HashAlgorithmStrategy {

  /**
   * Computes hash using this strategy's specific algorithm. The implementation must use secure
   * cryptographic libraries and handle all error conditions appropriately.
   *
   * @param input Data to hash (must not be null)
   * @return Computed hash as byte array
   * @throws CryptographicException if computation fails or input is invalid
   */
  byte[] computeHash(String input) throws CryptographicException;

  /**
   * Gets the canonical algorithm name for this strategy. The name should match the standard Java
   * Cryptography Extension algorithm names.
   *
   * @return Algorithm name (e.g., "SHA-256", "SHA-3-256")
   */
  String getAlgorithmName();

  /**
   * Indicates if this algorithm is cryptographically secure according to current security
   * standards. Only secure algorithms should return true.
   *
   * @return true if algorithm is collision-resistant and secure, false otherwise
   */
  boolean isSecure();

  /**
   * Gets performance characteristics of this algorithm. This helps users understand the trade-offs
   * between security and performance.
   *
   * @return Performance rating enum value
   */
  PerformanceRating getPerformanceRating();

  /**
   * Gets a human-readable description of the algorithm including its security properties and use
   * cases.
   *
   * @return Description suitable for user display
   */
  String getDescription();
}
