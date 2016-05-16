package com.rizandoelrizo.ij.server.service;

import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.repository.UserRepository;

import java.util.Optional;

public class RoleAuthorizationService implements AuthorizationService{

    private final UserRepository userRepository;

    public RoleAuthorizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isUserInRole(String username, Role expectedRole) {
        boolean isUserInRoles = false;
        Optional<User> optionalUser = userRepository.findByName(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            isUserInRoles = user.hasRole(expectedRole);
        }

        return isUserInRoles;
    }

}
