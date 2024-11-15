package com.thantuan.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private String address;
    private Set<String> roles;
    private String authProvider;
    private boolean enabled;
    private boolean accountLocked;
    private List<OrderItemDto> orderItemList;
}
