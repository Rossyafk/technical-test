package com.rizandoelrizo.ij.server.web.handler.rest;

import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.rizandoelrizo.ij.server.common.MimeType;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rizandoelrizo.ij.server.common.HttpHeader.ALLOW;
import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class RestHandler implements HttpHandler {

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), UTF_8))) {
            return buffer.lines().collect(Collectors.joining());
        }
    }

    protected boolean containsContentType(HttpExchange exchange, MimeType expectedMimeType) {
        Headers requestHeaders = exchange.getRequestHeaders();
        return requestHeaders.containsKey(CONTENT_TYPE.getName()) &&
                requestHeaders.get(CONTENT_TYPE.getName()).stream()
                        .anyMatch(contentType -> expectedMimeType.getName().equals(contentType));
    }

    protected void returnNotAuthorized(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HTTP_FORBIDDEN, -1);
        exchange.close();
    }

    protected void returnMethodNotAllowed(HttpExchange exchange, HttpMethod[] allowed) throws IOException {
        String allowedMethods = Stream.of(allowed)
                .map(Enum::name)
                .collect(Collectors.joining(","));
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(ALLOW.getName(), allowedMethods);
        exchange.sendResponseHeaders(HTTP_BAD_METHOD, -1);
        exchange.close();
    }

    protected void returnUnsupportedMediaType(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HTTP_UNSUPPORTED_TYPE, -1);
        exchange.close();
    }

}
