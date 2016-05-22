package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.UserRepository;

import java.util.Base64;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Default implementation of the AuthorizationService.
 */
public class AuthorizationServiceImpl implements AuthorizationService{

    private final UserRepository userRepository;

    public AuthorizationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Checks if the given username and password are valid.
     * @param username name of the user.
     * @param password password of the user.
     * @return true if valid, false otherwise.
     */
    @Override
    public boolean isValidCredential(String username, String password) {
        Optional<User> searchedUser = userRepository.findByName(username);
        if (searchedUser.isPresent()) {
            User user = searchedUser.get();
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes(UTF_8));
            return user.getPassword().equals(encodedPassword);
        }
        return false;
    }

    /**
     * Checks if the credentials of the given {@link User} are valid.
     * @param userToCheck {@link User} to check.
     * @return true if valid, false otherwise.
     */
    @Override
    public boolean isValidCredential(User userToCheck) {
        Optional<User> searchedUser = userRepository.findByName(userToCheck.getName());
        if (searchedUser.isPresent()) {
            User user = searchedUser.get();
            return user.getPassword().equals(userToCheck.getPassword());
        }
        return false;
    }

    /**
     * Checks if a given username contains a given {@link Role}
     * @param username name of the user.
     * @param expectedRole the role to check.
     * @return true if contained, false otherwise.
     */
    @Override
    public boolean isUserInRole(String username, Role expectedRole) {
        Optional<User> searchedUser = userRepository.findByName(username);
        if (searchedUser.isPresent()) {
            User user = searchedUser.get();
            return user.hasRole(expectedRole);
        }
        return false;
    }

}
