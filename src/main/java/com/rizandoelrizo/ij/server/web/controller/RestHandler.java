package com.rizandoelrizo.ij.server.web.controller;

import com.rizandoelrizo.ij.server.common.ContentType;
import com.rizandoelrizo.ij.server.common.HttpMethod;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.POST;
import static com.rizandoelrizo.ij.server.common.HttpRequestHeader.*;
import static com.rizandoelrizo.ij.server.common.HttpResponseHeader.ALLOW;
import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.UserService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpPrincipal;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestHandler implements HttpHandler {

    private final HttpMethod[] allowedHttpMethods = new HttpMethod[]{GET, POST};

    private final UserService userService;

    private final AuthorizationService authorizationService;

    public RestHandler(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    public void handle(HttpExchange exchange) throws IOException {

        HttpMethod requestMethod = HttpMethod.valueOf(exchange.getRequestMethod());

        switch (requestMethod) {
            case GET:
                doGet(exchange);
                break;
            case POST:
                doProtectedPost(exchange, Role.ADMIN);
                break;
            default:
                returnMethodNotAllowed(exchange, allowedHttpMethods);
        }

    }

    private void doGet(HttpExchange exchange) throws IOException {
        List<User> users = userService.findAll();
        String response = users.toString();
        exchange.sendResponseHeaders(HTTP_OK, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes(UTF_8));
        os.close();
    }

    private void doProtectedPost(HttpExchange exchange, Role expectedRole) throws IOException {
        HttpPrincipal principal = exchange.getPrincipal();
        if (authorizationService.isUserInRole(principal.getUsername(), expectedRole)) {
            doPost(exchange);
        } else {
            returnNotAuthorized(exchange);
        }
    }

    private void doPost(HttpExchange exchange) throws IOException {
        if (!containsContentType(exchange, ContentType.FORM_URL_ENCODED)) {
            returnUnsupportedMediaType(exchange);
        } else {
            String requestBody = readRequestBody(exchange);

            Map<String, List<String>> postParameters = Stream.of(requestBody)
                    .map(body -> body.split("&"))
                    .flatMap(Stream::of)
                    .map(pair -> pair.split("="))
                    .peek(Arrays::toString)
                    .filter(pairArray -> pairArray.length > 1)
                    .collect(Collectors.toMap(
                            pairArray -> pairArray[0],
                            pairArray -> Arrays.asList(pairArray[1]),
                            (s, a) -> Stream.concat(s.stream(), a.stream()).collect(Collectors.toList())
                    ));

            System.out.println(postParameters);

        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), UTF_8))) {
            return buffer.lines().collect(Collectors.joining());
        }
    }

    private boolean containsContentType(HttpExchange exchange, ContentType expectedContentType) {
        Headers requestHeaders = exchange.getRequestHeaders();
        if (requestHeaders.containsKey(CONTENT_TYPE.getName())) {
            return requestHeaders.get(CONTENT_TYPE.getName())
                    .stream()
                    .anyMatch(contentType -> expectedContentType.getName().equals(contentType));
        } else {
            return false;
        }
    }

    private void returnMethodNotAllowed(HttpExchange exchange, HttpMethod[] allowed) throws IOException {
        String allowedMethods = Stream.of(allowed)
                .map(Enum::name)
                .collect(Collectors.joining(","));
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(ALLOW.getName(), allowedMethods);
        exchange.sendResponseHeaders(HTTP_BAD_METHOD, -1);
    }

    private void returnNotAuthorized(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HTTP_FORBIDDEN, -1);
    }

    private void returnUnsupportedMediaType(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HTTP_UNSUPPORTED_TYPE, -1);
    }

}
