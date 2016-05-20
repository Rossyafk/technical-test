package com.rizandoelrizo.ij.server.common;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    ALLOW("Allow"),
    LOCATION("Location"),
    WWW_AUTHENTICATE("WWW-Authenticate"),
    AUTHORIZATION("Authorization");

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
