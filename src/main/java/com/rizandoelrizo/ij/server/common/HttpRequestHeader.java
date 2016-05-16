package com.rizandoelrizo.ij.server.common;

public enum HttpRequestHeader {
    CONTENT_TYPE("Content-Type");

    private final String name;

    HttpRequestHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
