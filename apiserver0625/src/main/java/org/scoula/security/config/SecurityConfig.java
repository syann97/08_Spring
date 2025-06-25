package org.scoula.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.scoula.security.filter.AuthenticationErrorFilter;
import org.scoula.security.filter.JwtAuthenticationFilter;
import org.scoula.security.filter.JwtUsernamePasswordAuthenticationFilter;
import org.scoula.security.handler.CustomAccessDeniedHandler;
import org.scoula.security.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;


@Configuration
@EnableWebSecurity
@Slf4j
@MapperScan(basePackages = {"org.scoula.security.account.mapper"})
@ComponentScan(basePackages = {"org.scoula.security"})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired // 로그인 처리 커스텀 필터
    private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

    // 사용자 정보 조회 커스텀 서비스
    private final UserDetailsService userDetailsService;

    // 인증 에러 처리 필터
    private final AuthenticationErrorFilter authenticationErrorFilter;

    // JWT 토큰 검증 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 403 Forbidden 처리 핸들러
    private final CustomAccessDeniedHandler accessDeniedHandler;

    // 401 Unauthorized 처리 핸들러
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;



    /**
     * AuthenticationManager 빈 등록
     */
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * BCrypt 패스워드 인코더 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 구성 설정
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)      // 사용자 정보 서비스 설정
                .passwordEncoder(passwordEncoder());         // 패스워드 인코더 설정
    }

    /**
     * CORS 설정 - 모든 도메인 허용
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);               // 인증 정보 포함 허용
        config.addAllowedOriginPattern("*");            // 모든 도메인 허용
        config.addAllowedHeader("*");                   // 모든 헤더 허용
        config.addAllowedMethod("*");                   // 모든 HTTP 메서드 허용

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * 정적 리소스 접근 제한 무시 설정
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/*", "/api/member/**");
    }

    /**
     * UTF-8 문자 인코딩 필터
     */
    public CharacterEncodingFilter encodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return encodingFilter;
    }

    /**
     * HTTP 보안 설정 - 메인 보안 구성
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {

        // 1. 필터 체인 설정 (순서 중요!)
        http.addFilterBefore(encodingFilter(), CsrfFilter.class)                                    // UTF-8 인코딩
                .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class) // 인증 에러 처리
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)   // JWT 토큰 인증
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 로그인 처리

        // 2. 예외 처리 설정
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)  // 401 에러 처리
                .accessDeniedHandler(accessDeniedHandler);           // 403 에러 처리

        // 3. 경로별 접근 권한 설정
        http.authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()                                    // CORS preflight 허용
                .antMatchers("/api/security/all").permitAll()                                   // 모든 사용자 접근 허용
                .antMatchers("/api/security/member").access("hasRole('ROLE_MEMBER')")          // MEMBER 권한 필요
                .antMatchers("/api/security/admin").access("hasRole('ROLE_ADMIN')")            // ADMIN 권한 필요
                .anyRequest().authenticated();                                                   // 나머지는 인증 필요

        // 4. 보안 기능 비활성화 설정 (JWT 사용을 위해)
        http.httpBasic().disable()                                                          // 기본 HTTP 인증 비활성화
                .csrf().disable()                                                               // CSRF 보호 비활성화
                .formLogin().disable()                                                          // Form 로그인 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);   // 세션 사용 안함 (Stateless)
    }
}