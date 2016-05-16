package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.Role;

public interface AuthorizationService {

    boolean isUserInRole(String username, Role expectedRole);

}
