package com.snhu.sslserver.exception;

/**
 * Custom exception for cryptographic operations in the checksum verification system.
 * This exception provides structured error handling with error codes and user-friendly messages
 * while maintaining security by not exposing sensitive internal details.
 * 
 * @author CS305 Student
 * @version 1.0
 */
public class CryptographicException extends Exception {
    
    private final ErrorCode errorCode;
    private final String userMessage;
    
    /**
     * Constructs a CryptographicException with error code and user message.
     * 
     * @param errorCode The specific error code categorizing the failure
     * @param userMessage A safe message that can be displayed to users
     */
    public CryptographicException(ErrorCode errorCode, String userMessage) {
        super(userMessage);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    /**
     * Constructs a CryptographicException with error code, user message, and underlying cause.
     * 
     * @param errorCode The specific error code categorizing the failure
     * @param userMessage A safe message that can be displayed to users
     * @param cause The underlying exception that caused this error
     */
    public CryptographicException(ErrorCode errorCode, String userMessage, Throwable cause) {
        super(userMessage, cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
    }
    
    /**
     * Gets the error code associated with this exception.
     * 
     * @return The ErrorCode enum value
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets the user-safe message for this exception.
     * 
     * @return A message safe to display to end users
     */
    public String getUserMessage() {
        return userMessage;
    }
}