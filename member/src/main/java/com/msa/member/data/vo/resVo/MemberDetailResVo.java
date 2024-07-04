package com.msa.member.data.vo.resVo;

import com.msa.member.data.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MemberDetailResVo {
    @Schema(description = "이름")
    private final String name;

    @Schema(description = "로그인 ID")
    private final String username;

    @Schema(description = "이름")
    private final String phoneNumber;

    public MemberDetailResVo(Member member) {
        this.name = member.getName();
        this.username = member.getUsername();
        this.phoneNumber = member.getPhoneNumber();
    }
}
