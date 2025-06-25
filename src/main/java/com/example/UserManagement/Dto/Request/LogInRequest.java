package com.example.UserManagement.Dto.Request;

import com.example.UserManagement.validator.CustomPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogInRequest {
    @NotBlank
    private String email;

    @NotBlank
    @CustomPassword
    private String password;
}
