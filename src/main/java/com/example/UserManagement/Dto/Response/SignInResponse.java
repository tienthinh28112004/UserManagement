package com.example.UserManagement.Dto.Response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
    private Long userId;

    private String accessToken;

    private Long accessTokenExpireIn;

    private List<String> roles;
}
