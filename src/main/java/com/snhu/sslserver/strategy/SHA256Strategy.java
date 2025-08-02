package com.snhu.sslserver.strategy;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;
import com.snhu.sslserver.model.PerformanceRating;
import com.snhu.sslserver.provider.ICryptographicProvider;

/**
 * SHA-256 hash algorithm strategy implementation. SHA-256 is the recommended default algorithm
 * providing excellent balance of security and performance.
 *
 * <p>SHA-256 characteristics: - 256-bit hash output - Collision-resistant - NIST approved - Widely
 * supported across platforms - Excellent performance for most use cases
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class SHA256Strategy implements HashAlgorithmStrategy {

  private static final String ALGORITHM_NAME = "SHA-256";
  private static final String DESCRIPTION =
      "SHA-256: NIST-approved algorithm with excellent security-to-performance ratio. "
          + "Recommended for general use with 256-bit output and collision resistance.";

  private final ICryptographicProvider cryptographicProvider;

  /**
   * Constructor with dependency injection.
   *
   * @param cryptographicProvider Provider for cryptographic operations
   */
  public SHA256Strategy(ICryptographicProvider cryptographicProvider) {
    this.cryptographicProvider =
        Objects.requireNonNull(cryptographicProvider, "Cryptographic provider cannot be null");
  }

  @Override
  public byte[] computeHash(String input) throws CryptographicException {
    Objects.requireNonNull(input, "Input cannot be null");

    try {
      MessageDigest digest = cryptographicProvider.createDigest(ALGORITHM_NAME);
      byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
      return digest.digest(inputBytes);
    } catch (Exception e) {
      throw new CryptographicException(
          ErrorCode.COMPUTATION_FAILED, "Failed to compute SHA-256 hash: " + e.getMessage(), e);
    }
  }

  @Override
  public String getAlgorithmName() {
    return ALGORITHM_NAME;
  }

  @Override
  public boolean isSecure() {
    return true;
  }

  @Override
  public PerformanceRating getPerformanceRating() {
    return PerformanceRating.FAST;
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
