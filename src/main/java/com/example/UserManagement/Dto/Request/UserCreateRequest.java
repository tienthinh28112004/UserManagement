package com.example.UserManagement.Dto.Request;

import com.example.UserManagement.validator.CustomPassword;
import com.example.UserManagement.validator.MinSize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateRequest {
    @NotBlank
    @MinSize(min=8)
    private String fullName;

    @NotBlank
    @MinSize(min=8)
    private String email;

    @NotBlank
    @CustomPassword()
    private String password;
}
