package com.msa.member.data.vo.resVo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SignInResVo {
    @Schema(description = "액세스 토큰")
    private final String accessToken;

    @Schema(description = "리프레쉬 토큰")
    private final String refreshToken;

    public SignInResVo(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
