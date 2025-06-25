package com.example.UserManagement.Dto.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class PageResponse<T> implements Serializable {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private Long totalElements;

    private T items;
}
