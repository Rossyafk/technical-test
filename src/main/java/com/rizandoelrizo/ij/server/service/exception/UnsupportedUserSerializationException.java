package com.rizandoelrizo.ij.server.service.exception;

/**
 * Exception thrown when something is wrong during the serialization of a user.
 */
public class UnsupportedUserSerializationException extends Exception {

    public UnsupportedUserSerializationException() {
    }

    public UnsupportedUserSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

}
