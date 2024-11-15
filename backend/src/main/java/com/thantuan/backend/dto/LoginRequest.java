package com.thantuan.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginRequest {
    @NotBlank(message = "Email is required!")
    @Email(message = "Email should be valid!")
    private String email;

    @NotBlank(message = "Password is required!")
    private String password;
}
