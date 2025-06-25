package com.example.UserManagement.Utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public class SecurityUtils {
    private  SecurityUtils(){
    }
    public  static Optional<String> getCurrentLogin(){
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication= context.getAuthentication();

        //lấy thông tin email nếu đăng nhập thông thường
        if(authentication.getPrincipal() instanceof UserDetails userDetails){
            return Optional.ofNullable(userDetails.getUsername());//
        }

        //lấy thông tin email nếu đng nập bằng jwt
        if(authentication.getPrincipal() instanceof Jwt jwt){
            return Optional.ofNullable(jwt.getSubject());
        }

        //lấy thông tin email(cáu này ít dùng)
        if(authentication.getPrincipal() instanceof  String s){
            return Optional.ofNullable(s);
        }
        return Optional.empty();
    }


}
