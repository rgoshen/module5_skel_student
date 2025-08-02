package com.snhu.sslserver.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable data model representing the result of a hash computation.
 * This class encapsulates all relevant information about a hash operation
 * including the original data, algorithm used, computed hash, and metadata.
 * 
 * @author Rick Goshen
 * @version 1.0
 */
public class HashResult {
    
    private final String originalData;
    private final String algorithm;
    private final String hexHash;
    private final Instant timestamp;
    private final long computationTimeMs;
    
    /**
     * Constructs a HashResult with all required fields.
     * 
     * @param originalData The original input data that was hashed
     * @param algorithm The cryptographic algorithm used
     * @param hexHash The computed hash in hexadecimal format
     * @param timestamp When the hash was computed
     * @param computationTimeMs Time taken to compute the hash in milliseconds (must be non-negative)
     * @throws IllegalArgumentException if computationTimeMs is negative
     */
    public HashResult(String originalData, String algorithm, String hexHash, 
                     Instant timestamp, long computationTimeMs) {
        if (originalData == null) {
            throw new IllegalArgumentException("Original data cannot be null");
        }
        if (algorithm == null) {
            throw new IllegalArgumentException("Algorithm cannot be null");
        }
        if (hexHash == null) {
            throw new IllegalArgumentException("Hex hash cannot be null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        
        this.originalData = originalData;
        this.algorithm = algorithm;
        this.hexHash = hexHash;
        this.timestamp = timestamp;
        
        if (computationTimeMs < 0) {
            throw new IllegalArgumentException("Computation time cannot be negative: " + computationTimeMs);
        }
        this.computationTimeMs = computationTimeMs;
    }
    
    /**
     * Gets the original data that was hashed.
     * 
     * @return The original input data
     */
    public String getOriginalData() {
        return originalData;
    }
    
    /**
     * Gets the algorithm used for hash computation.
     * 
     * @return The algorithm name (e.g., "SHA-256")
     */
    public String getAlgorithm() {
        return algorithm;
    }
    
    /**
     * Gets the computed hash in hexadecimal format.
     * 
     * @return The hash as a lowercase hexadecimal string
     */
    public String getHexHash() {
        return hexHash;
    }
    
    /**
     * Gets the timestamp when the hash was computed.
     * 
     * @return The computation timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the time taken to compute the hash.
     * 
     * @return Computation time in milliseconds
     */
    public long getComputationTimeMs() {
        return computationTimeMs;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        HashResult that = (HashResult) obj;
        return computationTimeMs == that.computationTimeMs &&
               Objects.equals(originalData, that.originalData) &&
               Objects.equals(algorithm, that.algorithm) &&
               Objects.equals(hexHash, that.hexHash) &&
               Objects.equals(timestamp, that.timestamp);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(originalData, algorithm, hexHash, timestamp, computationTimeMs);
    }
    
    @Override
    public String toString() {
        return "HashResult{" +
               "originalData='" + originalData + '\'' +
               ", algorithm='" + algorithm + '\'' +
               ", hexHash='" + hexHash + '\'' +
               ", timestamp=" + timestamp +
               ", computationTimeMs=" + computationTimeMs +
               '}';
    }
    
    /**
     * Builder class for constructing HashResult instances.
     * Provides a fluent interface for creating HashResult objects.
     */
    public static class Builder {
        private String originalData;
        private String algorithm;
        private String hexHash;
        private Instant timestamp;
        private long computationTimeMs;
        
        public Builder originalData(String originalData) {
            this.originalData = originalData;
            return this;
        }
        
        public Builder algorithm(String algorithm) {
            this.algorithm = algorithm;
            return this;
        }
        
        public Builder hexHash(String hexHash) {
            this.hexHash = hexHash;
            return this;
        }
        
        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder computationTimeMs(long computationTimeMs) {
            if (computationTimeMs < 0) {
                throw new IllegalArgumentException("Computation time cannot be negative: " + computationTimeMs);
            }
            this.computationTimeMs = computationTimeMs;
            return this;
        }
        
        public HashResult build() {
            return new HashResult(originalData, algorithm, hexHash, timestamp, computationTimeMs);
        }
    }
    
    /**
     * Creates a new Builder instance for constructing HashResult objects.
     * 
     * @return A new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }
}