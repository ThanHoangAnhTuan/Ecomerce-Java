package com.thantuan.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private CategoryDtoResponse category;
    private UserDto user;
    private int inventory;
    private int quantity;
}
