package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.Role;
import com.rizandoelrizo.ij.server.model.User;
import com.rizandoelrizo.ij.server.service.exception.UnsupportedUserSerializationException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.rizandoelrizo.ij.server.common.MimeType.FORM_URL_ENCODED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Default implementation of the UserSerializationService.
 */
public class UserSerializationServiceImpl implements UserSerializationService {

    private static final String ATTRIBUTE_SEPARATOR = "&";

    private static final String PAIR_SEPARATOR = "=";

    /**
     * Deserialize a given URL Encoded string into the corresponding user instance.
     * @param urlEncodedBody the URL Encoded string.
     * @return the corresponding user instance.
     * @throws UnsupportedUserSerializationException if the syntax of the URL Encoded string is wrong.
     */
    @Override
    public User deserialize(String urlEncodedBody) throws UnsupportedUserSerializationException {
        String validBody = Optional.ofNullable(urlEncodedBody).orElseThrow(UnsupportedUserSerializationException::new);
        String urlDecodedBody = getUrlDecodedBody(validBody);
        Map<User.Attribute, List<String>> bodyParams = getBodyParamsMap(urlDecodedBody);

        Optional<String> parsedName = Optional.empty();
        Optional<String> parsedPassword = Optional.empty();
        List<String> parsedRawRoles = new ArrayList<>();

        for (Map.Entry<User.Attribute, List<String>> param : bodyParams.entrySet()) {
            switch (param.getKey()) {
                case NAME:
                    parsedName = Optional.ofNullable(param.getValue().iterator().next());
                    break;
                case PASSWORD:
                    parsedPassword = Optional.ofNullable(param.getValue().iterator().next());
                    break;
                default:
                    parsedRawRoles.addAll(param.getValue());
            }
        }

        String name = parsedName.orElseThrow(UnsupportedUserSerializationException::new);
        String password = parsedPassword.orElseThrow(UnsupportedUserSerializationException::new);
        Set<Role> roles = getRoles(parsedRawRoles);
        return getUser(name, password, roles);
    }

    private String getUrlDecodedBody(String urlEncodedBody) throws UnsupportedUserSerializationException {
        String decodedBody;
        try {
            decodedBody = URLDecoder.decode(urlEncodedBody, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw exception("Invalid URL Encoded body", e);
        }
        return decodedBody;
    }

    private Map<User.Attribute, List<String>> getBodyParamsMap(String urlDecodedBody) throws UnsupportedUserSerializationException {
        Map<User.Attribute, List<String>> bodyParams;
        try {
            bodyParams = Stream.of(urlDecodedBody)
                    .map(body -> body.split(ATTRIBUTE_SEPARATOR))
                    .flatMap(Stream::of)
                    .map(attribute -> attribute.split(PAIR_SEPARATOR))
                    .filter(pairArray -> pairArray.length > 1)
                    .collect(toMap(
                            pairArray -> User.Attribute.valueOf(pairArray[0].toUpperCase()),
                            pairArray -> Collections.singletonList(pairArray[1]),
                            (s, a) -> Stream.concat(s.stream(), a.stream())
                                    .collect(toList())
                    ));
        }catch (IllegalArgumentException e) {
            throw exception(String.format("Invalid '%s' syntax", FORM_URL_ENCODED.getName()), e);
        }
        return bodyParams;
    }

    private Set<Role> getRoles(List<String> parsedRawRoles) throws UnsupportedUserSerializationException {
        Set<Role> roles = new HashSet<>();
        try {
            roles.addAll(parsedRawRoles.stream()
                    .map(rawRole -> Role.valueOf(rawRole.toUpperCase()))
                    .collect(toSet()));
        }catch (IllegalArgumentException e) {
            throw exception("Invalid 'Role' value", e);
        }
        return roles;
    }

    private User getUser(String name, String password, Set<Role> roles) throws UnsupportedUserSerializationException {
        User user;
        try {
            user = User.of(name, password, roles);
        } catch (IllegalArgumentException e) {
            throw exception("Invalid 'User' attributes", e);
        }
        return user;
    }

    private UnsupportedUserSerializationException exception(String message, Throwable cause) {
        return new UnsupportedUserSerializationException(message, cause);
    }

}
