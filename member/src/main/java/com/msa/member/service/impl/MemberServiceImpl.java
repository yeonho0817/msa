package com.msa.member.service.impl;

import com.msa.member.config.security.JwtProvider;
import com.msa.member.data.entity.Member;
import com.msa.member.data.enumerate.Permission;
import com.msa.member.data.repository.MemberRepository;
import com.msa.member.data.vo.SignInReqVo;
import com.msa.member.data.vo.SignUpReqVo;
import com.msa.member.data.vo.resVo.MemberDetailResVo;
import com.msa.member.data.vo.resVo.SignInResVo;
import com.msa.member.error.Error;
import com.msa.member.error.ErrorSpec;
import com.msa.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public String getToken() {
        return jwtProvider.generateAccessToken("admin", 1L, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Transactional
    @Override
    public void signUp(SignUpReqVo reqVo) {
        if(memberRepository.existsByUsernameAndDeleted(reqVo.getUsername(), false)) {
            throw Error.of(ErrorSpec.AlreadyExistAccount);
        }

        memberRepository.save(new Member(reqVo.getName(), reqVo.getUsername(),
                passwordEncoder.encode(reqVo.getPassword()), reqVo.getPhoneNumber(), Permission.USER));
    }

    @Transactional
    @Override
    public SignInResVo signIn(SignInReqVo reqVo) {
        Member member = memberRepository.findByUsernameAndDeletedAndEnabledAndAccountNonLocked(
                reqVo.getUsername(), false, false, false
        ).orElseThrow(() -> Error.of(ErrorSpec.NotExistAccount));

        if(!passwordEncoder.matches(reqVo.getPassword(), member.getPassword())) {
            throw Error.of(ErrorSpec.InvalidPasswordError);
        }

        final String accessToken = jwtProvider.generateAccessToken(member.getUsername(), member.getId(), member.getAuthorities());
        final String refreshToken = jwtProvider.generateRefreshToken();

        return new SignInResVo(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    @Override
    public MemberDetailResVo detail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> Error.of(ErrorSpec.NotExistAccount));

        return new MemberDetailResVo(member);
    }
}
