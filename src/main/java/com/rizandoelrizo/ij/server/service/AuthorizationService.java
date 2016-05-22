package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;

/**
 * Interface responsible of the user authorization.
 */
public interface AuthorizationService {

    /**
     * Checks if the given username and password are valid.
     * @param username name of the user.
     * @param password password of the user.
     * @return true if valid, false otherwise.
     */
    boolean isValidCredential(String username, String password);

    /**
     * Checks if the credentials of the given {@link User} are valid.
     * @param userToCheck {@link User} to check.
     * @return true if valid, false otherwise.
     */
    boolean isValidCredential(User userToCheck);

    /**
     * Checks if a given username contains a given {@link Role}
     * @param username name of the user.
     * @param expectedRole the {@link Role} to check.
     * @return true if contained, false otherwise.
     */
    boolean isUserInRole(String username, Role expectedRole);

}
