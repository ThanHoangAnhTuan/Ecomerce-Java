package com.thantuan.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.thantuan.backend.entity.Payment;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {
    private List<OrderItemRequest> orderItemList;
    private Payment paymentInfo;
}
