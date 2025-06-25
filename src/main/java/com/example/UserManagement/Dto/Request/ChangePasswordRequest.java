package com.example.UserManagement.Dto.Request;

import com.example.UserManagement.validator.CustomPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotBlank(message = "email cannot be null")
    private String email;

    @CustomPassword
    private String password;

    @CustomPassword
    private String newPassword;

    @CustomPassword
    private String newPasswordConfirm;
}
