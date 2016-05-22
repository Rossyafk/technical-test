package com.rizandoelrizo.ij.server.web.security;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.HttpExchange;

import java.net.HttpCookie;

public class ViewAuthentication extends Authenticator
{
    @Override
    public Result authenticate(HttpExchange httpExchange) {

        HttpCookie cookie = new HttpCookie("", "");

        return null;
    }
}
