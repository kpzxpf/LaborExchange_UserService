package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.entity.EmailVerificationToken;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.event.EmailVerificationEvent;
import com.vlz.laborexchange_userservice.kafka.EmailVerificationProducer;
import com.vlz.laborexchange_userservice.repository.EmailVerificationTokenRepository;
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
class EmailVerificationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleService roleService;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock private com.vlz.laborexchange_userservice.repository.PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private EmailVerificationProducer emailVerificationProducer;
    @Mock private com.vlz.laborexchange_userservice.kafka.PasswordResetProducer passwordResetProducer;

    @InjectMocks
    private UserService userService;

    private static final Long USER_ID = 1L;
    private static final String EMAIL = "user@example.com";

    private User testUser() {
        return User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username("testuser")
                .emailVerified(false)
                .build();
    }

    // ================== initiateEmailVerification ==================

    @Test
    @DisplayName("initiateEmailVerification: удаляет старый токен, сохраняет новый, отправляет событие")
    void initiateEmailVerification_HappyPath() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser()));
        when(emailVerificationTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.initiateEmailVerification(USER_ID);

        verify(emailVerificationTokenRepository).deleteByUserId(USER_ID);
        ArgumentCaptor<EmailVerificationToken> tokenCaptor = ArgumentCaptor.forClass(EmailVerificationToken.class);
        verify(emailVerificationTokenRepository).save(tokenCaptor.capture());
        assertThat(tokenCaptor.getValue().getUserId()).isEqualTo(USER_ID);
        assertThat(tokenCaptor.getValue().getToken()).isNotBlank();
        assertThat(tokenCaptor.getValue().getExpiresAt()).isAfter(LocalDateTime.now());

        ArgumentCaptor<EmailVerificationEvent> eventCaptor = ArgumentCaptor.forClass(EmailVerificationEvent.class);
        verify(emailVerificationProducer).send(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("initiateEmailVerification: выбрасывает EntityNotFoundException если пользователь не найден")
    void initiateEmailVerification_UserNotFound_Throws() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.initiateEmailVerification(USER_ID))
                .isInstanceOf(com.vlz.laborexchange_userservice.exception.EntityNotFoundException.class);
    }

    // ================== verifyEmail ==================

    @Test
    @DisplayName("verifyEmail: устанавливает emailVerified=true при корректном токене")
    void verifyEmail_ValidToken_SetsEmailVerified() {
        String token = "valid-token";
        EmailVerificationToken evt = EmailVerificationToken.builder()
                .userId(USER_ID)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        when(emailVerificationTokenRepository.findByToken(token)).thenReturn(Optional.of(evt));
        User user = testUser();
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        userService.verifyEmail(token);

        assertThat(user.isEmailVerified()).isTrue();
        assertThat(evt.isUsed()).isTrue();
        verify(userRepository).save(user);
        verify(emailVerificationTokenRepository).save(evt);
    }

    @Test
    @DisplayName("verifyEmail: выбрасывает 400 если токен не найден")
    void verifyEmail_TokenNotFound_Throws400() {
        when(emailVerificationTokenRepository.findByToken("bad")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.verifyEmail("bad"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    @DisplayName("verifyEmail: выбрасывает 409 если токен уже использован")
    void verifyEmail_TokenAlreadyUsed_Throws409() {
        EmailVerificationToken evt = EmailVerificationToken.builder()
                .userId(USER_ID).token("t").expiresAt(LocalDateTime.now().plusHours(1)).used(true).build();
        when(emailVerificationTokenRepository.findByToken("t")).thenReturn(Optional.of(evt));
        assertThatThrownBy(() -> userService.verifyEmail("t"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409");
    }

    @Test
    @DisplayName("verifyEmail: выбрасывает 410 если токен истёк")
    void verifyEmail_TokenExpired_Throws410() {
        EmailVerificationToken evt = EmailVerificationToken.builder()
                .userId(USER_ID).token("t").expiresAt(LocalDateTime.now().minusMinutes(1)).used(false).build();
        when(emailVerificationTokenRepository.findByToken("t")).thenReturn(Optional.of(evt));
        assertThatThrownBy(() -> userService.verifyEmail("t"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("410");
    }
}
