package com.rizandoelrizo.ij.server.common;

/**
 * Common Mime Types.
 */
public enum MimeType {
    FORM_URL_ENCODED("application/x-www-form-urlencoded"),
    TEXT_PLAIN_UTF8("text/plain; charset=UTF-8"),
    TEXT_HTML_UTF8("text/html; charset=UTF-8"),
    WILDCARD("*/*");

    private final String name;

    MimeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
