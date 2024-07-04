package com.msa.member.service;

import com.msa.member.data.vo.SignInReqVo;
import com.msa.member.data.vo.SignUpReqVo;
import com.msa.member.data.vo.resVo.MemberDetailResVo;
import com.msa.member.data.vo.resVo.SignInResVo;

public interface MemberService {
    String getToken();
    void signUp(SignUpReqVo reqVo);
    SignInResVo signIn(SignInReqVo reqVo);
    MemberDetailResVo detail(Long memberId);
}
