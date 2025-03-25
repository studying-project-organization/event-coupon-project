package com.plusproject.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AccessTokenResponse {

    private final String accessToken;

    public static AccessTokenResponse of(String accessToken) {
        return new AccessTokenResponse(accessToken);
    }

    @Builder
    private AccessTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
