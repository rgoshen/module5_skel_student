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
 * SHA-512 hash algorithm strategy implementation. SHA-512 provides higher security margin with
 * 512-bit output, suitable for high-security scenarios.
 *
 * <p>SHA-512 characteristics: - 512-bit hash output - Higher security margin than SHA-256 - NIST
 * approved - Good performance on 64-bit systems - Suitable for high-security applications
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class SHA512Strategy implements HashAlgorithmStrategy {

  private static final String ALGORITHM_NAME = "SHA-512";
  private static final String DESCRIPTION =
      "SHA-512: High-security algorithm with 512-bit output and enhanced security margin. "
          + "Ideal for applications requiring maximum security with good performance on 64-bit systems.";

  private final ICryptographicProvider cryptographicProvider;

  /**
   * Constructor with dependency injection.
   *
   * @param cryptographicProvider Provider for cryptographic operations
   */
  public SHA512Strategy(ICryptographicProvider cryptographicProvider) {
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
          ErrorCode.COMPUTATION_FAILED, "Failed to compute SHA-512 hash: " + e.getMessage(), e);
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
