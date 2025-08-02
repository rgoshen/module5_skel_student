package com.snhu.sslserver.model;

/**
 * Enumeration representing performance characteristics of cryptographic algorithms. This rating
 * helps users understand the trade-offs between security and performance when selecting hash
 * algorithms.
 *
 * @author Rick Goshen
 * @version 1.0
 */
public enum PerformanceRating {

  /**
   * Fast algorithms with excellent performance characteristics. Suitable for high-throughput
   * scenarios where performance is critical.
   */
  FAST("Fast - Excellent performance for high-throughput scenarios"),

  /**
   * Medium performance algorithms that balance speed and security. Good general-purpose choice for
   * most applications.
   */
  MEDIUM("Medium - Good balance of performance and security"),

  /**
   * Slower algorithms that prioritize maximum security. Best for high-security scenarios where
   * performance is less critical.
   */
  SLOW("Slow - Maximum security, suitable for high-security scenarios");

  private final String description;

  /**
   * Constructs a PerformanceRating with a descriptive message.
   *
   * @param description Human-readable description of the performance rating
   */
  PerformanceRating(String description) {
    this.description = description;
  }

  /**
   * Gets the human-readable description of this performance rating.
   *
   * @return Description suitable for user display
   */
  public String getDescription() {
    return description;
  }
}
