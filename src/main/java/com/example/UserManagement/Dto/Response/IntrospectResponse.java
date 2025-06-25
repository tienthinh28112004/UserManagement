package com.example.UserManagement.Dto.Response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectResponse {
    private boolean valid;

    private List<String> role;
}
