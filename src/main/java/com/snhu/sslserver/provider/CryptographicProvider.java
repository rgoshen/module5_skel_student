package com.snhu.sslserver.provider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.snhu.sslserver.exception.CryptographicException;
import com.snhu.sslserver.exception.ErrorCode;

/**
 * Implementation of ICryptographicProvider that provides secure cryptographic operations
 * and algorithm management. This class ensures only secure algorithms are used and
 * provides efficient utility methods for cryptographic operations.
 * 
 * Thread-safe implementation with algorithm caching for performance optimization.
 * Explicitly rejects deprecated algorithms (MD5, SHA-1) to maintain security standards.
 * 
 * @author Rick Goshen
 * @version 1.0
 */
@Component
public class CryptographicProvider implements ICryptographicProvider {
    
    /**
     * Set of potentially secure algorithms that we want to support.
     * Actual availability will be checked at runtime.
     */
    private static final Set<String> POTENTIALLY_SECURE_ALGORITHMS;
    
    static {
        Set<String> algorithms = new HashSet<>();
        algorithms.add("SHA-256");
        algorithms.add("SHA-384");
        algorithms.add("SHA-512");
        algorithms.add("SHA-3-256");
        algorithms.add("SHA-3-384");
        algorithms.add("SHA-3-512");
        POTENTIALLY_SECURE_ALGORITHMS = Collections.unmodifiableSet(algorithms);
    }
    
    /**
     * Cache of actually available secure algorithms in the current JVM.
     * Populated lazily on first access.
     */
    private volatile Set<String> availableSecureAlgorithms;
    
    /**
     * Set of deprecated algorithms that are explicitly rejected.
     * These algorithms have known vulnerabilities and must not be used.
     */
    private static final Set<String> DEPRECATED_ALGORITHMS;
    
    static {
        Set<String> deprecated = new HashSet<>();
        deprecated.add("MD5");
        deprecated.add("SHA-1");
        DEPRECATED_ALGORITHMS = Collections.unmodifiableSet(deprecated);
    }
    
    /**
     * Lookup table for efficient byte-to-hex conversion.
     * Using a lookup table provides better performance than string formatting.
     */
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    
    /**
     * Cache for algorithm availability checks to avoid repeated JCE queries.
     * Thread-safe concurrent map for performance in multi-threaded environments.
     */
    private final ConcurrentMap<String, Boolean> algorithmAvailabilityCache = new ConcurrentHashMap<>();
    
    /**
     * Initializes the set of available secure algorithms by testing each
     * potentially secure algorithm against the current JVM.
     * 
     * @return Set of algorithms that are both secure and available
     */
    private Set<String> initializeAvailableSecureAlgorithms() {
        return POTENTIALLY_SECURE_ALGORITHMS.stream()
            .filter(this::isAlgorithmActuallyAvailable)
            .collect(Collectors.toSet());
    }
    
    /**
     * Checks if an algorithm is actually available in the current JVM
     * without throwing exceptions.
     * 
     * @param algorithm Algorithm name to check
     * @return true if algorithm is available, false otherwise
     */
    private boolean isAlgorithmActuallyAvailable(String algorithm) {
        try {
            MessageDigest.getInstance(algorithm);
            return true;
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    /**
     * Gets the set of secure algorithms that are available in the current JVM.
     * Uses double-checked locking for thread-safe lazy initialization.
     * 
     * @return Set of available secure algorithms
     */
    private Set<String> getAvailableSecureAlgorithms() {
        if (availableSecureAlgorithms == null) {
            synchronized (this) {
                if (availableSecureAlgorithms == null) {
                    availableSecureAlgorithms = initializeAvailableSecureAlgorithms();
                }
            }
        }
        return availableSecureAlgorithms;
    }
    
    @Override
    public MessageDigest createDigest(String algorithm) throws CryptographicException {
        Objects.requireNonNull(algorithm, "Algorithm cannot be null");
        
        String normalizedAlgorithm = algorithm.trim().toUpperCase();
        
        // Check if algorithm is deprecated/insecure
        if (DEPRECATED_ALGORITHMS.contains(normalizedAlgorithm)) {
            throw new CryptographicException(
                ErrorCode.ALGORITHM_INSECURE,
                String.format("Algorithm '%s' is deprecated and insecure. Use SHA-256 or newer.", algorithm)
            );
        }
        
        // Check if algorithm is in our secure list
        Set<String> secureAlgorithms = getAvailableSecureAlgorithms();
        if (!secureAlgorithms.contains(normalizedAlgorithm)) {
            throw new CryptographicException(
                ErrorCode.ALGORITHM_NOT_SUPPORTED,
                String.format("Algorithm '%s' is not supported. Supported algorithms: %s", 
                    algorithm, String.join(", ", secureAlgorithms))
            );
        }
        
        // Attempt to create MessageDigest instance
        try {
            return MessageDigest.getInstance(normalizedAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptographicException(
                ErrorCode.ALGORITHM_NOT_SUPPORTED,
                String.format("Algorithm '%s' is not available in the current Java environment", algorithm),
                e
            );
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
        return Collections.unmodifiableSet(new HashSet<>(getAvailableSecureAlgorithms()));
    }
    
    @Override
    public Set<String> getDeprecatedAlgorithms() {
        return Collections.unmodifiableSet(new HashSet<>(DEPRECATED_ALGORITHMS));
    }
    
    @Override
    public boolean isAlgorithmAvailable(String algorithm) {
        Objects.requireNonNull(algorithm, "Algorithm cannot be null");
        
        String normalizedAlgorithm = algorithm.trim().toUpperCase();
        
        // Use cached result if available
        return algorithmAvailabilityCache.computeIfAbsent(normalizedAlgorithm, alg -> {
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
        
        String normalizedAlgorithm = algorithm.trim().toUpperCase();
        
        // Algorithm is secure if it's in our available secure list and not deprecated
        return getAvailableSecureAlgorithms().contains(normalizedAlgorithm) && 
               !DEPRECATED_ALGORITHMS.contains(normalizedAlgorithm);
    }
}