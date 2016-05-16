package com.rizandoelrizo.ij.server.web.security;

import com.rizandoelrizo.ij.server.HttpServerApp;
import com.sun.net.httpserver.BasicAuthenticator;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestAuthentication extends BasicAuthenticator {

    private final static Logger LOG = Logger.getLogger(HttpServerApp.class.getName());

    public RestAuthentication(String realm) {
        super(realm);
    }

    @Override
    public boolean checkCredentials(String user, String password) {
        LOG.log(Level.INFO, "Checking credentials for user {0}", user);

        Optional<String> optionalUser = Optional.ofNullable(user);
        Optional<String> optionalPassword = Optional.ofNullable(password);

        if (optionalUser.isPresent() && optionalPassword.isPresent()) {
            return true;
        }

        return false;
    }

}
