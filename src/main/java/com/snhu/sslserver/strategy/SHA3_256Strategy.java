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
 * SHA-3-256 hash algorithm strategy implementation. SHA-3-256 is the latest NIST standard with
 * quantum-resistant design principles.
 *
 * <p>SHA-3-256 characteristics: - 256-bit hash output - Latest NIST standard (2015) -
 * Quantum-resistant design - Different internal structure from SHA-2 family - Slightly slower than
 * SHA-256 but more future-proof
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class SHA3_256Strategy implements HashAlgorithmStrategy {

  private static final String ALGORITHM_NAME = "SHA-3-256";
  private static final String DESCRIPTION =
      "SHA-3-256: Latest NIST standard with quantum-resistant design. "
          + "Future-proof algorithm with 256-bit output, ideal for high-security applications.";

  private final ICryptographicProvider cryptographicProvider;

  /**
   * Constructor with dependency injection.
   *
   * @param cryptographicProvider Provider for cryptographic operations
   */
  public SHA3_256Strategy(ICryptographicProvider cryptographicProvider) {
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
          ErrorCode.COMPUTATION_FAILED, "Failed to compute SHA-3-256 hash: " + e.getMessage(), e);
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
    return PerformanceRating.MEDIUM;
  }

  @Override
  public String getDescription() {
    return DESCRIPTION;
  }
}
