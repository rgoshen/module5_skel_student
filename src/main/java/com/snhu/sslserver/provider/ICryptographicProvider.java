package com.snhu.sslserver.provider;

import java.security.MessageDigest;
import java.util.Set;

import com.snhu.sslserver.exception.CryptographicException;

/**
 * Interface for cryptographic operations and algorithm management. This interface abstracts the
 * underlying Java Cryptography Extension (JCE) operations and provides secure algorithm validation
 * and utility methods.
 *
 * <p>Implementations must ensure: - Only secure algorithms are supported - Proper MessageDigest
 * instance management - Efficient byte-to-hex conversion - Thread-safe operations
 *
 * @author Rick Goshen
 * @version 1.0
 */
public interface ICryptographicProvider {

  /**
   * Creates a MessageDigest instance for the specified algorithm. Only secure algorithms are
   * supported. Deprecated algorithms (MD5, SHA-1) will result in a CryptographicException being
   * thrown.
   *
   * @param algorithm Algorithm name (must be secure and supported)
   * @return Configured MessageDigest instance ready for use
   * @throws CryptographicException if algorithm is unsupported, insecure, or unavailable
   */
  MessageDigest createDigest(String algorithm) throws CryptographicException;

  /**
   * Converts byte array to hexadecimal string representation. The output is lowercase hexadecimal
   * format suitable for display and comparison operations.
   *
   * @param bytes Byte array to convert (typically hash output)
   * @return Lowercase hexadecimal string representation
   * @throws IllegalArgumentException if bytes array is null
   */
  String bytesToHex(byte[] bytes);

  /**
   * Gets the set of cryptographically secure algorithms supported by this provider. This list
   * includes only algorithms that are collision-resistant and meet current security standards.
   *
   * @return Immutable set of secure algorithm names
   */
  Set<String> getSecureAlgorithms();

  /**
   * Gets the set of deprecated algorithms that are explicitly rejected. These algorithms have known
   * vulnerabilities and must not be used for security-critical operations.
   *
   * @return Immutable set of deprecated algorithm names
   */
  Set<String> getDeprecatedAlgorithms();

  /**
   * Validates if the specified algorithm is available in the current Java Cryptography Extension
   * environment.
   *
   * @param algorithm Algorithm name to check
   * @return true if algorithm is available in JCE, false otherwise
   */
  boolean isAlgorithmAvailable(String algorithm);

  /**
   * Validates if the specified algorithm is cryptographically secure according to current security
   * standards.
   *
   * @param algorithm Algorithm name to validate
   * @return true if algorithm is secure, false if deprecated or insecure
   */
  boolean isAlgorithmSecure(String algorithm);
}
