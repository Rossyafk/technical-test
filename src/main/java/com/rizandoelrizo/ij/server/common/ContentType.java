package com.rizandoelrizo.ij.server.common;

public enum ContentType {
    FORM_URL_ENCODED("application/x-www-form-urlencoded");

    private final String name;

    ContentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
