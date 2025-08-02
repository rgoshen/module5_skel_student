package com.snhu.sslserver.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.snhu.sslserver.strategy.HashAlgorithmStrategy;

/**
 * Factory class for creating and managing hash algorithm strategy instances. This factory provides
 * centralized strategy management and supports algorithm lookup by name.
 *
 * <p>The factory automatically discovers all available strategy implementations through dependency
 * injection and provides efficient lookup mechanisms.
 *
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class HashAlgorithmFactory {

  private final Map<String, HashAlgorithmStrategy> strategies;

  /**
   * Constructor that initializes the factory with all available strategy implementations.
   *
   * @param strategyList List of all available hash algorithm strategies (injected by Spring)
   */
  public HashAlgorithmFactory(List<HashAlgorithmStrategy> strategyList) {
    Objects.requireNonNull(strategyList, "Strategy list cannot be null");

    this.strategies = new HashMap<>();
    for (HashAlgorithmStrategy strategy : strategyList) {
      // Use uppercase algorithm name for case-insensitive lookup
      String algorithmName = strategy.getAlgorithmName().toUpperCase();
      strategies.put(algorithmName, strategy);
    }
  }

  /**
   * Creates a strategy instance for the specified algorithm name.
   *
   * @param algorithmName The name of the algorithm (case-insensitive)
   * @return HashAlgorithmStrategy instance for the specified algorithm
   * @throws IllegalArgumentException if the algorithm is not supported
   */
  public HashAlgorithmStrategy createStrategy(String algorithmName) {
    Objects.requireNonNull(algorithmName, "Algorithm name cannot be null");

    String normalizedName = algorithmName.trim().toUpperCase();
    HashAlgorithmStrategy strategy = strategies.get(normalizedName);

    if (strategy == null) {
      throw new IllegalArgumentException(
          String.format(
              "Unsupported algorithm: %s. Supported algorithms: %s",
              algorithmName, String.join(", ", strategies.keySet())));
    }

    return strategy;
  }

  /**
   * Gets a strategy instance for the specified algorithm name, returning Optional to handle missing
   * algorithms gracefully.
   *
   * @param algorithmName The name of the algorithm (case-insensitive)
   * @return Optional containing the strategy if found, empty otherwise
   */
  public Optional<HashAlgorithmStrategy> getStrategy(String algorithmName) {
    if (algorithmName == null) {
      return Optional.empty();
    }

    String normalizedName = algorithmName.trim().toUpperCase();
    return Optional.ofNullable(strategies.get(normalizedName));
  }

  /**
   * Checks if the specified algorithm is supported by this factory.
   *
   * @param algorithmName The name of the algorithm to check (case-insensitive)
   * @return true if the algorithm is supported, false otherwise
   */
  public boolean isAlgorithmSupported(String algorithmName) {
    if (algorithmName == null) {
      return false;
    }

    String normalizedName = algorithmName.trim().toUpperCase();
    return strategies.containsKey(normalizedName);
  }

  /**
   * Gets all available strategy instances.
   *
   * @return List of all available hash algorithm strategies
   */
  public List<HashAlgorithmStrategy> getAllStrategies() {
    return List.copyOf(strategies.values());
  }
}
