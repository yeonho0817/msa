package com.msa.member.config.security;

import com.msa.member.service.security.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final AuthUserDetailsService userService;
    private final JwtProvider jwtProvider;
    private final HandlerExceptionResolver resolver;
    private static final String[] PERMIT_ALL = {
            "/member/sign-up",
            "/member/sign-in",
            "/member/check-id",
            "/member/sign-in/oauth/kakao",
            "/member/sign-in/oauth/kakao/callback",
            "/member/sign-in/oauth/google",
            "/member/sign-in/oauth/google/callback",
    };

    @Value("${oAuth.endpoints.web.cors.allowed-origins}")
    private String strAllowedOrigins;

    public SecurityConfig(
            AuthUserDetailsService userService,
            JwtProvider jwtProvider,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.resolver = resolver;
    }

    /**
     * password 암호화 bean
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * AuthenticationWrapperFilter bean 등록
     *
     * @return AuthenticationWrapperFilter
     */
    @Bean
    public AuthenticationWrapperFilter authenticationWrapperFilter() {
        return new AuthenticationWrapperFilter(resolver);
    }

    /**
     * JwtAuthenticationFilter bean 등록
     *
     * @return JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        JwtAuthenticationFilter jwtAuthenticationFilter =
                new JwtAuthenticationFilter(userService, jwtProvider);

        // filter 가 적용 되지 않을 url 세팅
        jwtAuthenticationFilter.setShouldNotFilterPredicate(request ->
                Arrays.stream(PERMIT_ALL).anyMatch(url -> url.equals(request.getRequestURI()))
        );
        return jwtAuthenticationFilter;
    }

    /**
     * Resource 용 SecurityFilterChain bean 등록
     *
     * @param http: http security
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    @Order(0)
    public SecurityFilterChain resources(HttpSecurity http) throws Exception {
        // resource, swagger 등 정적 파일 ignore 설정
        http.requestMatchers(matchers -> {
                    matchers.requestMatchers(PathRequest.toStaticResources().atCommonLocations());
                    matchers.antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**");
                }
        );

        // 권한은 검증하지 않고, 요청은 보호한다.
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

        // request cache 사용하지 않음
        http.requestCache(RequestCacheConfigurer::disable);

        // security context 사용하지 않음
        http.securityContext(AbstractHttpConfigurer::disable);

        // session management 사용하지 않음
        http.sessionManagement(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * 메인 SecurityFilterChain bean 등록
     *
     * @param http: http security
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 동일한 origin 만 허용
        http.headers().frameOptions().sameOrigin();

        // csrf(html tag 를 통한 공격) disabled
        http.csrf().disable();

        http.cors().configurationSource(corsConfigurationSource());

        // http basic disabled (기본 설정은 enable => 미인증시 id, pwd 기반 로그인폼으로 리다이렉트 됨)
        http.httpBasic().disable();

        // security 제공 logout 사용하지 않음
        http.logout().disable();

        // JWT 방식을 사용하기 때문에 session 방식 사용하지 않음
        http.sessionManagement().sessionCreationPolicy(STATELESS);

        // url 인증 권한 처리 설정
        http.authorizeRequests(authorizeRequest -> {
            // AnonymousCallable 설정
            authorizeRequest.antMatchers(PERMIT_ALL).permitAll();
            authorizeRequest.anyRequest().authenticated();
            // 그 외의 모든 api 는 user 또는 admin 권한이 필요함. (config 에 설정한 role name 이 USER 일 경우, 실제 role name 은 ROLE_USER 여야 한다.)
//            authorizeRequest.anyRequest()
//                .hasAnyRole(Permission.USER.getDescription(), Permission.ADMIN.getDescription());
        });

        // security error handle
        http.exceptionHandling(exceptionHandling -> {
            // 인증이 안된 사용자가 특정 권한이 필요한 자원에 접근하려고 할 때 호출
            exceptionHandling.authenticationEntryPoint(new JwtEntryPoint(resolver));
            // 인증이 된 사용자가 갖고있지 않은 특정 권한이 필요한 자원에 접근하려고 할 때 호출
            exceptionHandling.accessDeniedHandler(new JwtAccessDeniedHandler(resolver));
        });

        // security 인증 filter 적용
        http
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authenticationWrapperFilter(), JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        if(!ObjectUtils.isEmpty(strAllowedOrigins)) {
            for(String cors : strAllowedOrigins.split(",")) {
                configuration.addAllowedOrigin(cors);
            }
        }
        configuration.addAllowedMethod(CorsConfiguration.ALL);
        configuration.addAllowedHeader(CorsConfiguration.ALL);
        configuration.addExposedHeader("Authorization");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
