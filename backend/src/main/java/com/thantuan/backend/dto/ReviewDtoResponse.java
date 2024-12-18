package com.thantuan.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDtoResponse {
    private Long id;
    private String content;
    private int rating;
    private ProductDto product;
    private UserDto user;
    private LocalDateTime createAt;
}
