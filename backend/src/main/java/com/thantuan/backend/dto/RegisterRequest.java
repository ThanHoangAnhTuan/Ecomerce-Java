package com.thantuan.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {
    @NotBlank(message = "Username is required!")
    private String username;

    @NotBlank(message = "Password is required!")
    @Pattern(regexp = "^.{8,}$", message = "Password must be at least 8 characters long.")
    @Pattern(regexp = "^(?=.*[a-z]).+$", message = "Password must contain at least one lowercase" +
            " letter.")
    @Pattern(regexp = "^(?=.*[A-Z]).+$", message = "Password must contain at least one uppercase" +
            " letter.")
    @Pattern(regexp = "^(?=.*\\d).+$", message = "Password must contain at least one digit.")
    @Pattern(regexp = "^(?=.*[!@#$%^&*(),.?\":{}|<>]).+$", message = "Password must contain at least one " +
            "special character.")
    private String password;

    @Email(message = "Email should be valid!")
    @NotBlank(message = "Email is required!")
    private String email;

    private String phone;

    private String address;
}
