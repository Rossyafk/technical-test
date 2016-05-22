package com.rizandoelrizo.ij.server.web.security;

import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.sun.net.httpserver.BasicAuthenticator;

import java.util.Optional;

/**
 * BasicAuthenticator for REST requests.
 */
public class RestAuthenticator extends BasicAuthenticator {

    private final AuthorizationService authorizationService;

    public RestAuthenticator(String realm, AuthorizationService authorizationService) {
        super(realm);
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        Optional<String> optionalUser = Optional.ofNullable(username);
        Optional<String> optionalPassword = Optional.ofNullable(password);

        return optionalUser.isPresent() && optionalPassword.isPresent() &&
                authorizationService.isValidCredential(username, password);
    }

}
