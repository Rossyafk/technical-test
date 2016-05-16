package com.rizandoelrizo.ij.server.common;

public enum HttpResponseHeader {
    ALLOW("Allow");

    private final String name;

    HttpResponseHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
