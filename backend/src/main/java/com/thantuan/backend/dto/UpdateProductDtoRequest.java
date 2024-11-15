package com.thantuan.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateProductDtoRequest {
    @NotNull(message = "Category is required!")
    private Long categoryId;

    @NotBlank(message = "Name is required!")
    private String name;

    private String description;

    @NotNull(message = "Price is required!")
    private BigDecimal price;

    private MultipartFile file;

    @NotNull(message = "Inventory is required!")
    private int inventory;
}
