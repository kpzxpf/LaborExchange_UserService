package com.vlz.laborexchange_userservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetEmailEvent {
    private Long userId;
    private String email;
    private String token;
}
