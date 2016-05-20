package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.UserRepository;

import java.util.Base64;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AuthorizationServiceImpl implements AuthorizationService{

    private final UserRepository userRepository;

    public AuthorizationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
