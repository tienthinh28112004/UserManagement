package com.example.UserManagement.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig{
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH"));
        corsConfiguration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept-Language","x-no-retry","Access-Control-Allow-Origin"));//client gửi các header cụ thể trong request
        corsConfiguration.setExposedHeaders(List.of("Authorization","Content-Type","Accept-Language"));//client đọc các header cụ thể từ response

        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(source);
    }
}
