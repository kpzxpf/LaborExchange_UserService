package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.ForgotPasswordRequest;
import com.vlz.laborexchange_userservice.dto.ResetPasswordRequest;
import com.vlz.laborexchange_userservice.entity.PasswordResetToken;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.event.PasswordResetEmailEvent;
import com.vlz.laborexchange_userservice.kafka.PasswordResetProducer;
import com.vlz.laborexchange_userservice.repository.EmailVerificationTokenRepository;
import com.vlz.laborexchange_userservice.repository.PasswordResetTokenRepository;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleService roleService;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private com.vlz.laborexchange_userservice.kafka.EmailVerificationProducer emailVerificationProducer;
    @Mock private PasswordResetProducer passwordResetProducer;

    @InjectMocks
    private UserService userService;

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "user@example.com";

    private User testUser() {
        return User.builder().id(USER_ID).email(EMAIL).username("testuser").build();
    }

    // ================== initiatePasswordReset ==================

    @Test
    @DisplayName("initiatePasswordReset: удаляет старый токен, сохраняет новый, отправляет событие")
    void initiatePasswordReset_HappyPath() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(EMAIL);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser()));
        when(passwordResetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.initiatePasswordReset(request);

        verify(passwordResetTokenRepository).deleteByUserId(USER_ID);
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(tokenCaptor.getValue().getToken()).isNotBlank();
        assertThat(tokenCaptor.getValue().getExpiresAt()).isAfter(LocalDateTime.now());

        ArgumentCaptor<PasswordResetEmailEvent> eventCaptor = ArgumentCaptor.forClass(PasswordResetEmailEvent.class);
        verify(passwordResetProducer).send(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("initiatePasswordReset: выбрасывает 404 если email не найден")
    void initiatePasswordReset_EmailNotFound_Throws404() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("unknown@example.com");
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.initiatePasswordReset(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404");
    }

    // ================== resetPassword ==================

    @Test
    @DisplayName("resetPassword: устанавливает новый пароль при корректном токене")
    void resetPassword_ValidToken_UpdatesPassword() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("valid-token");
        request.setNewPassword("newPass123");

        PasswordResetToken prt = PasswordResetToken.builder()
                .userId(USER_ID).token("valid-token")
                .expiresAt(LocalDateTime.now().plusHours(1)).used(false).build();
        User user = testUser();
        user.setPassword("oldHash");

        when(passwordResetTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(prt));
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPass123")).thenReturn("newHash");

        userService.resetPassword(request);

        assertThat(user.getPassword()).isEqualTo("newHash");
        assertThat(prt.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).save(prt);
    }

    @Test
    @DisplayName("resetPassword: выбрасывает 400 если токен не найден")
    void resetPassword_TokenNotFound_Throws400() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("bad");
        request.setNewPassword("pass");
        when(passwordResetTokenRepository.findByToken("bad")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.resetPassword(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    @DisplayName("resetPassword: выбрасывает 409 если токен уже использован")
    void resetPassword_TokenAlreadyUsed_Throws409() {
        PasswordResetToken prt = PasswordResetToken.builder()
                .userId(USER_ID).token("t").expiresAt(LocalDateTime.now().plusHours(1)).used(true).build();
        when(passwordResetTokenRepository.findByToken("t")).thenReturn(Optional.of(prt));
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("t");
        request.setNewPassword("pass");
        assertThatThrownBy(() -> userService.resetPassword(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    @DisplayName("resetPassword: выбрасывает 410 если токен истёк")
    void resetPassword_TokenExpired_Throws410() {
        PasswordResetToken prt = PasswordResetToken.builder()
                .userId(USER_ID).token("t").expiresAt(LocalDateTime.now().minusMinutes(1)).used(false).build();
        when(passwordResetTokenRepository.findByToken("t")).thenReturn(Optional.of(prt));
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("t");
        request.setNewPassword("pass");
        assertThatThrownBy(() -> userService.resetPassword(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("410");
    }
}
