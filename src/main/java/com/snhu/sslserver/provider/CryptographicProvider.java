package com.snhu.sslserver.provider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;

/**
 * Implementation of ICryptographicProvider that provides secure cryptographic operations and
 * algorithm management. This class ensures only secure algorithms are used and provides efficient
 * utility methods for cryptographic operations.
 *
 * <p>Thread-safe implementation with static algorithm initialization for optimal performance.
 * Explicitly rejects deprecated algorithms (MD5, SHA-1) to maintain security standards.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class CryptographicProvider implements ICryptographicProvider {

  /**
   * Set of deprecated algorithms that are explicitly rejected. These algorithms have known
   * vulnerabilities and must not be used.
   */
  private static final Set<String> DEPRECATED_ALGORITHMS;

  static {
    Set<String> deprecated = new HashSet<>();
    deprecated.add("MD5");
    deprecated.add("SHA-1");
    DEPRECATED_ALGORITHMS = Collections.unmodifiableSet(deprecated);
  }

  /**
   * Set of secure algorithms that are both cryptographically secure and available in the current
   * JVM. Computed once at class initialization for optimal performance.
   */
  private static final Set<String> SECURE_SUPPORTED_ALGORITHMS;

  static {
    Set<String> potentiallySecure = new HashSet<>();
    potentiallySecure.add("SHA-256");
    potentiallySecure.add("SHA-384");
    potentiallySecure.add("SHA-512");
    potentiallySecure.add("SHA-3-256");
    potentiallySecure.add("SHA-3-384");
    potentiallySecure.add("SHA-3-512");

    SECURE_SUPPORTED_ALGORITHMS =
        Collections.unmodifiableSet(
            potentiallySecure.stream()
                // Filter out deprecated algorithms first
                .filter(algorithm -> !DEPRECATED_ALGORITHMS.contains(algorithm))
                // Test availability once at startup
                .filter(
                    algorithm -> {
                      try {
                        MessageDigest.getInstance(algorithm);
                        return true;
                      } catch (NoSuchAlgorithmException e) {
                        return false;
                      }
                    })
                .collect(Collectors.toSet()));
  }

  /**
   * Lookup table for efficient byte-to-hex conversion. Using a lookup table provides better
   * performance than string formatting.
   */
  private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

  /**
   * Cache for algorithm availability checks to avoid repeated JCE queries. Thread-safe concurrent
   * map for performance in multi-threaded environments. Uses bounded size to prevent memory leaks.
   */
  private final ConcurrentMap<String, Boolean> algorithmAvailabilityCache =
      new ConcurrentHashMap<>();

  /** Maximum size for the algorithm availability cache to prevent unbounded memory growth. */
  private static final int MAX_CACHE_SIZE = 100;

  @Override
  public MessageDigest createDigest(String algorithm) throws CryptographicException {
    Objects.requireNonNull(algorithm, "Algorithm cannot be null");

    String normalizedAlgorithm = algorithm.trim().toUpperCase(Locale.ROOT);

    // Check if algorithm is deprecated/insecure
    if (DEPRECATED_ALGORITHMS.contains(normalizedAlgorithm)) {
      throw new CryptographicException(
          ErrorCode.ALGORITHM_INSECURE,
          String.format(
              "Algorithm '%s' is deprecated and insecure. Use SHA-256 or newer.", algorithm));
    }

    // Check if algorithm is in our secure supported list
    if (!SECURE_SUPPORTED_ALGORITHMS.contains(normalizedAlgorithm)) {
      throw new CryptographicException(
          ErrorCode.ALGORITHM_NOT_SUPPORTED,
          String.format(
              "Algorithm '%s' is not supported. Supported algorithms: %s",
              algorithm, String.join(", ", SECURE_SUPPORTED_ALGORITHMS)));
    }

    // Attempt to create MessageDigest instance
    try {
      return MessageDigest.getInstance(normalizedAlgorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new CryptographicException(
          ErrorCode.ALGORITHM_NOT_SUPPORTED,
          String.format(
              "Algorithm '%s' is not available in the current Java environment", algorithm),
          e);
    }
  }

  @Override
  public String bytesToHex(byte[] bytes) {
    Objects.requireNonNull(bytes, "Byte array cannot be null");

    char[] hexChars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      int v = bytes[i] & 0xFF;
      hexChars[i * 2] = HEX_ARRAY[v >>> 4];
      hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  @Override
  public Set<String> getSecureAlgorithms() {
    return SECURE_SUPPORTED_ALGORITHMS;
  }

  @Override
  public Set<String> getDeprecatedAlgorithms() {
    return DEPRECATED_ALGORITHMS;
  }

  @Override
  public boolean isAlgorithmAvailable(String algorithm) {
    Objects.requireNonNull(algorithm, "Algorithm cannot be null");

    String normalizedAlgorithm = algorithm.trim().toUpperCase(Locale.ROOT);

    // Prevent unbounded cache growth by checking size
    if (algorithmAvailabilityCache.size() >= MAX_CACHE_SIZE) {
      // Clear cache when it gets too large to prevent memory leaks
      algorithmAvailabilityCache.clear();
    }

    // Use cached result if available
    return algorithmAvailabilityCache.computeIfAbsent(
        normalizedAlgorithm,
        alg -> {
          try {
            MessageDigest.getInstance(alg);
            return true;
          } catch (NoSuchAlgorithmException e) {
            return false;
          }
        });
  }

  @Override
  public boolean isAlgorithmSecure(String algorithm) {
    Objects.requireNonNull(algorithm, "Algorithm cannot be null");

    String normalizedAlgorithm = algorithm.trim().toUpperCase(Locale.ROOT);

    // Algorithm is secure if it's in our secure supported list and not deprecated
    return SECURE_SUPPORTED_ALGORITHMS.contains(normalizedAlgorithm)
        && !DEPRECATED_ALGORITHMS.contains(normalizedAlgorithm);
  }
}
