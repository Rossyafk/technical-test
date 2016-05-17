package com.rizandoelrizo.ij.server.service;


import com.rizandoelrizo.ij.server.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserSerializationServiceImpl implements UserSerializationService {

    @Override
    public User deserialize(String requestBody) {

        Map<String, List<String>> bodyParams = Stream.of(requestBody)
                .map(body -> body.split("&"))
                .flatMap(Stream::of)
                .map(pair -> pair.split("="))
                .peek(Arrays::toString)
                .filter(pairArray -> pairArray.length > 1)
                .collect(Collectors.toMap(
                        pairArray -> pairArray[0],
                        pairArray -> Collections.singletonList(pairArray[1]),
                        (s, a) -> Stream.concat(s.stream(), a.stream())
                                .collect(Collectors.toList())
                ));

        System.out.println(bodyParams);

        return User.of("Paquito", "paquito", Optional.empty());
    }


}
