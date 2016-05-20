package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.Role;

public interface AuthorizationService {

    boolean isValidCredential(String username, String password);

    boolean isUserInRole(String username, Role expectedRole);

}
