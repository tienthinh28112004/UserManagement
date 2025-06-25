package com.example.UserManagement.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebSecurityConfig {
    private final CustomJwtDecoder customJwtDecoder;
    private static final String[] whiteList={
            "/api/v1/auth/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //Xử lý các endpoint public
        http.csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults());
        http.authorizeHttpRequests(request ->request
                .requestMatchers(whiteList).permitAll()
                .anyRequest().authenticated()
        ).sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //Xử lý các endpoint cần token
        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwtConfigurer -> jwtConfigurer.decoder(customJwtDecoder))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .accessDeniedHandler(new JwtAccessDined())
        );
        return http.build();
    }
    @Bean
    public PasswordEncoder PasswordEncoder(){
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("Authority");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");//loại bỏ tiền tố scope

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
