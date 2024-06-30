package com.msa.member.config.security;


import com.msa.member.util.Constants;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
@RequiredArgsConstructor
public final class JwtProvider {

    @Value("${oAuth.jwt.access-token.secret-key}")
    private String secretKey;

    @Value("${oAuth.jwt.refresh-token.secret-key}")
    private String refreshSecretKey;

    @Value("${oAuth.jwt.access-token.expired}")
    private long accessTokenValidTime;

    @Value("${oAuth.jwt.access-token.before-refresh}")
    private long accessTokenRefreshTime;

    @Value("${oAuth.jwt.refresh-token.expired}")
    private long refreshTokenValidTime;

    @PostConstruct
    private void init() {
        this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
        this.refreshSecretKey = Base64.getEncoder().encodeToString(this.refreshSecretKey.getBytes());
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public boolean needTokenRefresh(String token) {
        Claims claims = getClaimsFromToken(token);
        Date now = new Date();
        return !now.before(new Date(claims.getExpiration().getTime() - accessTokenRefreshTime*1000));
    }

    public String getSubject(String token) {
        return String.valueOf(getClaimsFromToken(token).getSubject());
    }

    public String generateAccessToken(String username, Long id, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .claim(Constants.MEMBER_ID, id)
                .claim(Constants.GRANTED_AUTHORITY, grantedAuthorities)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(now.getTime() + accessTokenValidTime * 1000))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
            .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
            .signWith(SignatureAlgorithm.HS256, refreshSecretKey)
            .compact();
    }

}
