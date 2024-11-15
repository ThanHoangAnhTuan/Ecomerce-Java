package com.thantuan.backend.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
    private int status;
    private String message;
    private int totalPage;
    private long totalElements;
    private List<String> validationErrors;

    private UserDto user;
    private List<UserDto> userList;

    private CategoryDtoResponse category;
    private List<CategoryDtoResponse> categoryList;

    private OrderItemDto orderItem;
    private List<OrderItemDto> orderItemList;

    private OrderDto order;
    private List<OrderDto> orderList;

    private ProductDto product;
    private List<ProductDto> productList;
}
