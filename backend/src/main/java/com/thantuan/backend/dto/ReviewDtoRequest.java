package com.thantuan.backend.dto;

import lombok.Data;

@Data
public class ReviewDtoRequest {
    String content;
    int rating;
    Long productId;
}
