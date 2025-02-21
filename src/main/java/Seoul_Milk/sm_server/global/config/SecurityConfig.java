package Seoul_Milk.sm_server.global.config;

import Seoul_Milk.sm_server.global.exception.CustomAuthenticationEntryPoint;
import Seoul_Milk.sm_server.global.jwt.JWTFilter;
import Seoul_Milk.sm_server.global.jwt.JWTUtil;
import Seoul_Milk.sm_server.global.jwt.LoginFilter;
import Seoul_Milk.sm_server.global.refresh.RefreshToken;
import Seoul_Milk.sm_server.login.repository.MemberRepository;
import Seoul_Milk.sm_server.login.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    // 인증이 필요하지 않은 URL 목록
    private final String[] allowedUrls = {
            "/",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/join",
            "/login",
    };

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 설정
        http.cors(Customizer.withDefaults());

        http
                .csrf(AbstractHttpConfigurer::disable);

        // 경로별 인가 설정
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers(allowedUrls).permitAll()
                .anyRequest().authenticated());

        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

        //필터 추가 LoginFilter()는 인자를 받음 (AuthenticationManager() 메소드에 authenticationConfiguration 객체를 넣어야 함) 따라서 등록 필요
        http
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, new RefreshToken(memberRepository, refreshRepository)), UsernamePasswordAuthenticationFilter.class);

        // 예외 처리 설정
        http.exceptionHandling(e -> e
                .authenticationEntryPoint(customAuthenticationEntryPoint));

        // 세션 처리(stateless로 관리)
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
