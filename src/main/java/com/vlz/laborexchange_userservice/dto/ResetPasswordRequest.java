package com.vlz.laborexchange_userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 6, message = "Пароль должен содержать не менее 6 символов")
    private String newPassword;
}
