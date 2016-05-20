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

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class UserSerializationServiceImpl implements UserSerializationService {

    @Override
    public User deserialize(String requestBody) throws UnsupportedUserSerializationException {
        String validBody = Optional.ofNullable(requestBody).orElseThrow(UnsupportedUserSerializationException::new);
        String decodedBody = getDecodedBody(validBody);
        Map<User.Attribute, List<String>> bodyParams = getAttributeListMap(decodedBody);

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

    @Override
    public String serialize(User user) {
        return null;
    }

    private String getDecodedBody(String requestBody) throws UnsupportedUserSerializationException {
        String decodedBody;
        try {
            decodedBody = URLDecoder.decode(requestBody, UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw customException(e);
        }
        return decodedBody;
    }

    private Map<User.Attribute, List<String>> getAttributeListMap(String decodedBody) throws UnsupportedUserSerializationException {
        Map<User.Attribute, List<String>> bodyParams;
        try {
            bodyParams = Stream.of(decodedBody)
                    .map(body -> body.split("&"))
                    .flatMap(Stream::of)
                    .map(pair -> pair.split("="))
                    .filter(pairArray -> pairArray.length > 1)
                    .collect(toMap(
                            pairArray -> User.Attribute.valueOf(pairArray[0].toUpperCase()),
                            pairArray -> Collections.singletonList(pairArray[1]),
                            (s, a) -> Stream.concat(s.stream(), a.stream())
                                    .collect(toList())
                    ));
        }catch (IllegalArgumentException e) {
            throw customException(e);
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
            throw customException(e);
        }
        return roles;
    }

    private User getUser(String name, String password, Set<Role> roles) throws UnsupportedUserSerializationException {
        User user;
        try {
            user = User.of(name, password, roles);
        } catch (IllegalArgumentException e) {
            throw customException(e);
        }
        return user;
    }

    private UnsupportedUserSerializationException customException(Throwable suppressedException) {
        UnsupportedUserSerializationException exception = new UnsupportedUserSerializationException();
        exception.addSuppressed(suppressedException);
        return exception;
    }

}
