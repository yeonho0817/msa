package com.msa.member.data.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SignInReqVo {
    @Schema(description = "로그인 ID")
    @NotBlank(message = "로그인 아이디가 비었습니다.")
    private String username;

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호가 비었습니다.")
    private String password;
}
