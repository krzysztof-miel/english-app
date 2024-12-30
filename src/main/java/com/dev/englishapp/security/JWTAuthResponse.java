package com.dev.englishapp.security;

import lombok.Data;

@Data
public class JWTAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";

}
