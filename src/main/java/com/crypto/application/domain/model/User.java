package com.crypto.application.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class User {

    private Long id;

    private String name;

    private String apiKey;

    private String secretKey;


}
