package com.snhu.sslserver.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable data model representing information about a cryptographic
 * algorithm.
 * This class provides metadata about hash algorithms including security status,
 * performance characteristics, and descriptive information.
 * 
 * @author Rick Goshen
 * @version 1.0
 */
public class AlgorithmInfo {

    private final String name;
    private final boolean secure;
    private final PerformanceRating performance;
    private final String description;
    private final Set<String> aliases;

    /**
     * Constructs an AlgorithmInfo with all required fields.
     * 
     * @param name        The canonical algorithm name
     * @param secure      Whether the algorithm is cryptographically secure
     * @param performance The performance rating of the algorithm
     * @param description Human-readable description of the algorithm
     * @param aliases     Alternative names for the algorithm
     */
    public AlgorithmInfo(String name, boolean secure, PerformanceRating performance,
            String description, Set<String> aliases) {
        this.name = Objects.requireNonNull(name, "Algorithm name cannot be null");
        this.secure = secure;
        this.performance = Objects.requireNonNull(performance, "Performance rating cannot be null");
        this.description = Objects.requireNonNull(description, "Description cannot be null");
        this.aliases = createImmutableSet(Objects.requireNonNull(aliases, "Aliases cannot be null"));
    }

    /**
     * Creates an immutable set, avoiding unnecessary copying if the input is
     * already immutable.
     * This optimization prevents performance overhead when dealing with already
     * immutable sets.
     * 
     * @param input The input set to make immutable
     * @return An immutable set
     */
    private static Set<String> createImmutableSet(Set<String> input) {
        // Check if it's already an immutable empty set
        if (input.isEmpty()) {
            return Collections.emptySet();
        }

        // For Java 8 compatibility, we'll use a simple approach:
        // Create a defensive copy and make it unmodifiable
        return Collections.unmodifiableSet(new HashSet<>(input));
    }

    /**
     * Gets the canonical algorithm name.
     * 
     * @return The algorithm name (e.g., "SHA-256")
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates if the algorithm is cryptographically secure.
     * 
     * @return true if the algorithm is secure, false if deprecated or insecure
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Gets the performance rating of the algorithm.
     * 
     * @return The performance rating enum value
     */
    public PerformanceRating getPerformance() {
        return performance;
    }

    /**
     * Gets the human-readable description of the algorithm.
     * 
     * @return A description suitable for user display
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the set of alternative names for this algorithm.
     * 
     * @return Immutable set of algorithm aliases
     */
    public Set<String> getAliases() {
        return aliases;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        AlgorithmInfo that = (AlgorithmInfo) obj;
        return secure == that.secure &&
                Objects.equals(name, that.name) &&
                performance == that.performance &&
                Objects.equals(description, that.description) &&
                Objects.equals(aliases, that.aliases);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, secure, performance, description, aliases);
    }

    @Override
    public String toString() {
        return "AlgorithmInfo{" +
                "name='" + name + '\'' +
                ", secure=" + secure +
                ", performance=" + performance +
                ", description='" + description + '\'' +
                ", aliases=" + aliases +
                '}';
    }

    /**
     * Builder class for constructing AlgorithmInfo instances.
     * Provides a fluent interface for creating AlgorithmInfo objects.
     */
    public static class Builder {
        private String name;
        private boolean secure;
        private PerformanceRating performance;
        private String description;
        private Set<String> aliases = Collections.emptySet();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder performance(PerformanceRating performance) {
            this.performance = performance;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder aliases(Set<String> aliases) {
            this.aliases = aliases;
            return this;
        }

        public AlgorithmInfo build() {
            return new AlgorithmInfo(name, secure, performance, description, aliases);
        }
    }

    /**
     * Creates a new Builder instance for constructing AlgorithmInfo objects.
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}