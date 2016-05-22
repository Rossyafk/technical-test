package com.rizandoelrizo.ij.server.web.handler.view;

import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rizandoelrizo.ij.server.common.HttpHeader.ALLOW;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;

/**
 * Base handler for specific VIEW handlers.
 */
public abstract class ViewHandler implements HttpHandler {

    protected void returnMethodNotAllowed(HttpExchange exchange, HttpMethod[] allowed) {
        String allowedMethods = Stream.of(allowed)
                .map(Enum::name)
                .collect(Collectors.joining(","));
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(ALLOW.getName(), allowedMethods);
        returnWithStatusCode(exchange, HTTP_BAD_METHOD);
    }

    private void returnWithStatusCode(HttpExchange exchange, int httpStatusCode) {
        try {
            exchange.sendResponseHeaders(httpStatusCode, -1);
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            exchange.close();
        }
    }

}
