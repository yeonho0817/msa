package com.msa.member.config.security;

import com.msa.member.data.entity.Member;
import com.msa.member.error.Error;
import com.msa.member.error.ErrorSpec;
import com.msa.member.service.security.AuthUserDetailsService;
import com.msa.member.util.Constants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Predicate;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * JWT 권한 체크용 filter
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUserDetailsService userService;
    private final JwtProvider jwtProvider;
    private Predicate<HttpServletRequest> shouldNotFilterPredicate;

    public JwtAuthenticationFilter(AuthUserDetailsService userService, JwtProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    public void setShouldNotFilterPredicate(Predicate<HttpServletRequest> shouldNotFilterPredicate) {
        this.shouldNotFilterPredicate = shouldNotFilterPredicate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        this.authenticate(request, response);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return shouldNotFilterPredicate.test(request);
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response) {
        final String token = this.getToken(request);
        if (token == null) {
            return;
        }

        Member member = (Member) userService.loadUserByUsername(jwtProvider.getSubject(token));

        checkValidToken(token);

        //token 갱신
        if(jwtProvider.needTokenRefresh(token)) {
            String newToken = jwtProvider.generateAccessToken(member.getUsername(), member.getId(), member.getAuthorities());
            response.setHeader(AUTHORIZATION, Constants.TOKEN_TYPE + " " + newToken);
        }
        processSecurity(request, member);
    }

    private String getToken(HttpServletRequest request) {
        final String token = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(token)) {
            return token.replace(Constants.TOKEN_TYPE + " ", "");
        }
        return null;
    }

    private void checkValidToken(String token) {
        if (!jwtProvider.isValidToken(token)) {
            throw Error.of(ErrorSpec.InvalidTokenError);
        }
    }

    private void processSecurity(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
}
