package com.example.UserManagement.Dto.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntrospectRequest {
    @NotBlank
    private String accessToken;
}
