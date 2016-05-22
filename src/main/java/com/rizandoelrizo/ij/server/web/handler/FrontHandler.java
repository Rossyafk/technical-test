package com.rizandoelrizo.ij.server.web.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * FrontHandler for API REST request.
 */
public class FrontHandler extends AbstractBaseHandler {

    private Map<Pattern, HttpHandler> handlerMappings = new HashMap<>();

    public FrontHandler(Map<Pattern, HttpHandler> handlerMappings) {
        this.handlerMappings.putAll(handlerMappings);
    }

    @Override
    public void handle(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();

        Optional<HttpHandler> matchedHandler = handlerMappings.entrySet().stream()
                .filter(entry -> patternMatches(entry.getKey(), path))
                .findFirst()
                .map(Map.Entry::getValue);

        try {
            if (matchedHandler.isPresent()) {
                matchedHandler.get().handle(exchange);
            } else {
                returnResourceNotFound(exchange);
            }
        } catch (Exception e) {
            returnInternalServerError(exchange);
        } finally {
            exchange.close();
        }
    }

    private boolean patternMatches(Pattern pattern, String path) {
        return pattern.matcher(path).matches();
    }

}
