package com.rizandoelrizo.ij.server.web.security;

import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.sun.net.httpserver.BasicAuthenticator;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestAuthentication extends BasicAuthenticator {

    private final static Logger LOG = Logger.getLogger(RestAuthentication.class.getName());

    private final AuthorizationService authorizationService;

    public RestAuthentication(String realm, AuthorizationService authorizationService) {
        super(realm);
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        LOG.log(Level.FINE, "Checking credentials for user {0}", username);
        Optional<String> optionalUser = Optional.ofNullable(username);
        Optional<String> optionalPassword = Optional.ofNullable(password);

        return optionalUser.isPresent() && optionalPassword.isPresent() &&
                authorizationService.isValidCredential(username, password);
    }

}
