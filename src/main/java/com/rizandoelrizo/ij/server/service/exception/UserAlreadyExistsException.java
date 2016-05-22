package com.rizandoelrizo.ij.server.service.exception;

import com.rizandoelrizo.ij.server.model.User;

/**
 * Exception thrown when a user with the same name already exists.
 */
public class UserAlreadyExistsException extends Exception {

    public UserAlreadyExistsException(User user) {
        super(String.format("User '%s' already exists", user.getName()));
    }
}
