package com.msa.member.service.impl;

import com.msa.member.config.security.JwtProvider;
import com.msa.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final JwtProvider jwtProvider;

    @Transactional(readOnly = true)
    @Override
    public String getToken() {
        return jwtProvider.generateAccessToken("admin", 1L, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
