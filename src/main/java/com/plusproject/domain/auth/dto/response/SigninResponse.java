package com.plusproject.domain.auth.dto.response;

import lombok.Getter;

@Getter
public class SigninResponse {

    private final String bearerToken;

    private SigninResponse(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public static SigninResponse of(String bearerToken) {
        return new SigninResponse(bearerToken);
    }
}