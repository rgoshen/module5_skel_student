package com.snhu.sslserver.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable data model representing the result of input validation operations.
 * This class encapsulates validation status, sanitized data, and any error
 * or warning messages generated during validation.
 * 
 * @author Rick Goshen
 * @version 1.0
 */
public class ValidationResult {

    private final boolean valid;
    private final String sanitizedData;
    private final List<String> errors;
    private final List<String> warnings;

    /**
     * Private constructor for creating ValidationResult instances.
     * Use static factory methods for construction.
     * 
     * @param valid         Whether the validation passed
     * @param sanitizedData The sanitized/cleaned data (null if validation failed)
     * @param errors        List of error messages (must be immutable)
     * @param warnings      List of warning messages (must be immutable)
     */
    private ValidationResult(boolean valid, String sanitizedData,
            List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.sanitizedData = sanitizedData;
        // No defensive copying needed since we control the inputs and ensure they're
        // immutable
        this.errors = errors;
        this.warnings = warnings;
    }

    /**
     * Indicates if the validation was successful.
     * 
     * @return true if validation passed, false otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Gets the sanitized data after validation processing.
     * This may be null if validation failed.
     * 
     * @return The sanitized data, or null if validation failed
     */
    public String getSanitizedData() {
        return sanitizedData;
    }

    /**
     * Gets the list of error messages from validation.
     * 
     * @return Immutable list of error messages (empty if no errors)
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * Gets the list of warning messages from validation.
     * 
     * @return Immutable list of warning messages (empty if no warnings)
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Indicates if there are any error messages.
     * 
     * @return true if errors exist, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Indicates if there are any warning messages.
     * 
     * @return true if warnings exist, false otherwise
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Creates a successful validation result with sanitized data.
     * 
     * @param sanitizedData The cleaned/validated data
     * @return ValidationResult indicating success
     */
    public static ValidationResult success(String sanitizedData) {
        return new ValidationResult(true, sanitizedData, Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Creates a successful validation result with sanitized data and warnings.
     * 
     * @param sanitizedData The cleaned/validated data
     * @param warnings      List of warning messages
     * @return ValidationResult indicating success with warnings
     */
    public static ValidationResult successWithWarnings(String sanitizedData, List<String> warnings) {
        return new ValidationResult(true, sanitizedData, Collections.emptyList(),
                Collections.unmodifiableList(new ArrayList<String>(warnings)));
    }

    /**
     * Creates a failed validation result with error messages.
     * 
     * @param errors List of error messages explaining the failure
     * @return ValidationResult indicating failure
     */
    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, null,
                Collections.unmodifiableList(new ArrayList<String>(errors)),
                Collections.emptyList());
    }

    /**
     * Creates a failed validation result with a single error message.
     * 
     * @param error Single error message explaining the failure
     * @return ValidationResult indicating failure
     */
    public static ValidationResult failure(String error) {
        return new ValidationResult(false, null, Collections.singletonList(error), Collections.emptyList());
    }

    /**
     * Creates a failed validation result with errors and warnings.
     * 
     * @param errors   List of error messages
     * @param warnings List of warning messages
     * @return ValidationResult indicating failure with additional warnings
     */
    public static ValidationResult failure(List<String> errors, List<String> warnings) {
        return new ValidationResult(false, null,
                Collections.unmodifiableList(new ArrayList<String>(errors)),
                Collections.unmodifiableList(new ArrayList<String>(warnings)));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        ValidationResult that = (ValidationResult) obj;
        return valid == that.valid &&
                Objects.equals(sanitizedData, that.sanitizedData) &&
                Objects.equals(errors, that.errors) &&
                Objects.equals(warnings, that.warnings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, sanitizedData, errors, warnings);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", sanitizedData='" + sanitizedData + '\'' +
                ", errors=" + errors +
                ", warnings=" + warnings +
                '}';
    }
}