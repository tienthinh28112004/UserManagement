package com.example.UserManagement.Dto.Request;

import com.example.UserManagement.validator.MinSize;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotBlank
    @MinSize(min = 8)
    private String fullName;

    @NotBlank
    @MinSize(min = 8)
    private String phoneNumber;

    private LocalDate dob;
}
