package com.rizandoelrizo.ij.server.web.handler.rest;

import com.rizandoelrizo.ij.server.common.HttpMethod;
import com.rizandoelrizo.ij.server.common.MimeType;
import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.AuthorizationService;
import com.rizandoelrizo.ij.server.service.UserSerializationService;
import com.rizandoelrizo.ij.server.service.UserService;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rizandoelrizo.ij.server.common.HttpHeader.ALLOW;
import static com.rizandoelrizo.ij.server.common.HttpHeader.CONTENT_TYPE;
import static com.rizandoelrizo.ij.server.common.HttpHeader.LOCATION;
import static com.rizandoelrizo.ij.server.common.HttpMethod.GET;
import static com.rizandoelrizo.ij.server.common.HttpMethod.POST;
import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static com.rizandoelrizo.ij.server.common.MimeType.TEXT_PLAIN_UTF8;
import static java.net.HttpURLConnection.HTTP_BAD_METHOD;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNSUPPORTED_TYPE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RestHandler implements HttpHandler {

    private final HttpMethod[] allowedHttpMethods = new HttpMethod[]{GET, POST};

    private final UserService userService;

    private final AuthorizationService authorizationService;

    private final UserSerializationService userSerializationService;

    public RestHandler(UserService userService, AuthorizationService authorizationService,
                       UserSerializationService userSerializationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
        this.userSerializationService = userSerializationService;
    }

    public void handle(HttpExchange exchange) {
        try {
            service(exchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void service(HttpExchange exchange) throws IOException {
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
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(CONTENT_TYPE.getName(), TEXT_PLAIN_UTF8.getName());

        List<User> users = userService.findAll();
        byte[] response = users.toString().getBytes(UTF_8);

        exchange.sendResponseHeaders(HTTP_OK, response.length);
        OutputStream os = exchange.getResponseBody();
        os.write(response);
        os.close();

        exchange.close();
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
        if (!containsContentType(exchange, FORM_URL_ENCODED)) {
            returnUnsupportedMediaType(exchange);
        } else {
            String requestBody = readRequestBody(exchange);
            User postedUser = userSerializationService.deserialize(requestBody);
            User savedUser = userService.save(postedUser);
            byte[] response = savedUser.toString().getBytes(UTF_8);

            Headers responseHeaders = exchange.getResponseHeaders();
            responseHeaders.add(CONTENT_TYPE.getName(), TEXT_PLAIN_UTF8.getName());
            responseHeaders.add(LOCATION.getName(), "/api/users/" + savedUser.getId());

            exchange.sendResponseHeaders(HTTP_CREATED, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();

            exchange.close();
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), UTF_8))) {
            return buffer.lines().collect(Collectors.joining());
        }
    }


    private boolean containsContentType(HttpExchange exchange, MimeType expectedMimeType) {
        Headers requestHeaders = exchange.getRequestHeaders();
        return requestHeaders.containsKey(CONTENT_TYPE.getName()) &&
                requestHeaders.get(CONTENT_TYPE.getName()).stream()
                        .anyMatch(contentType -> expectedMimeType.getName().equals(contentType));
    }

    private void returnMethodNotAllowed(HttpExchange exchange, HttpMethod[] allowed) throws IOException {
        String allowedMethods = Stream.of(allowed)
                .map(Enum::name)
                .collect(Collectors.joining(","));
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add(ALLOW.getName(), allowedMethods);
        exchange.sendResponseHeaders(HTTP_BAD_METHOD, -1);
        exchange.close();
    }

    private void returnNotAuthorized(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HTTP_FORBIDDEN, -1);
        exchange.close();
    }

    private void returnUnsupportedMediaType(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(HTTP_UNSUPPORTED_TYPE, -1);
        exchange.close();
    }

}
