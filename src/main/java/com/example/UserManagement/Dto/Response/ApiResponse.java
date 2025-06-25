package com.example.UserManagement.Dto.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)//đánh dấu chuyển về json
public class ApiResponse<T> {
    @Builder.Default
    private int code=1000;
    private String message;
    private T result;
}
