package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.ChangePasswordRequest;
import com.vlz.laborexchange_userservice.dto.ForgotPasswordRequest;
import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.dto.ResetPasswordRequest;
import com.vlz.laborexchange_userservice.dto.UserDto;
import com.vlz.laborexchange_userservice.entity.EmailVerificationToken;
import com.vlz.laborexchange_userservice.entity.PasswordResetToken;
import com.vlz.laborexchange_userservice.event.EmailVerificationEvent;
import com.vlz.laborexchange_userservice.event.PasswordResetEmailEvent;
import com.vlz.laborexchange_userservice.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.kafka.EmailVerificationProducer;
import com.vlz.laborexchange_userservice.kafka.PasswordResetProducer;
import com.vlz.laborexchange_userservice.repository.EmailVerificationTokenRepository;
import com.vlz.laborexchange_userservice.repository.PasswordResetTokenRepository;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationProducer emailVerificationProducer;
    private final PasswordResetProducer passwordResetProducer;

    @Transactional(readOnly = true)
    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean checkLogin(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return true;//passwordEncoder.matches(password, user.getPassword());
    }

    @Transactional
    public Long create(RegisterRequest registerRequest) {
        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .phoneNumber(registerRequest.getPhone())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(roleService.findByRoleName(registerRequest.getUserRole()))
                .build();

        return userRepository.save(user).getId();
    }

    @Caching(evict = {
        @CacheEvict(value = "users:profile", key = "#userDto.id"),
        @CacheEvict(value = "users:email", key = "#userDto.id")
    })
    @Transactional
    public User update(UserDto userDto) {
        User user = getById(userDto.getId());

        user.setUsername(userDto.getUsername());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        return userRepository.save(user);
    }

    @Cacheable(value = "users:email", key = "#id")
    @Transactional(readOnly = true)
    public String getEmailById(Long id) {
        return userRepository.getEmailById(id);
    }

    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        return userRepository.getUserIdByEmail(email);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found: id={}", id);
            return new EntityNotFoundException("User not found");
        });
    }

    @Cacheable(value = "users:profile", key = "#id")
    @Transactional(readOnly = true)
    public UserDto getUserProfile(Long id) {
        User user = getById(id);

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleName(user.getRole().getRoleName())
                .build();
    }

    @Cacheable(value = "users:profile", key = "#userId + ':username'")
    @Transactional(readOnly = true)
    public String getUsernameByUserId(Long userId) {
        return userRepository.findUsernameById(userId);
    }

    @Transactional
    public void initiateEmailVerification(Long userId) {
        User user = getById(userId);

        emailVerificationTokenRepository.deleteByUserId(userId);

        String token = UUID.randomUUID().toString();
        EmailVerificationToken evt = EmailVerificationToken.builder()
                .userId(userId)
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(24))
                .build();
        emailVerificationTokenRepository.save(evt);

        emailVerificationProducer.send(EmailVerificationEvent.builder()
                .userId(userId)
                .email(user.getEmail())
                .token(token)
                .build());

        log.info("Email verification initiated for user {}", userId);
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken evt = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token not found"));

        if (evt.isUsed()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token already used");
        }
        if (evt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Verification token expired");
        }

        User user = getById(evt.getUserId());
        user.setEmailVerified(true);
        userRepository.save(user);

        evt.setUsed(true);
        emailVerificationTokenRepository.save(evt);

        log.info("Email verified for user {}", evt.getUserId());
    }

    @Transactional
    public void initiatePasswordReset(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with this email not found"));

        passwordResetTokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken prt = PasswordResetToken.builder()
                .userId(user.getId())
                .token(token)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .build();
        passwordResetTokenRepository.save(prt);

        passwordResetProducer.send(PasswordResetEmailEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .token(token)
                .build());

        log.info("Password reset initiated for user {}", user.getId());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken prt = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password reset token not found"));

        if (prt.isUsed()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token already used");
        }
        if (prt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Password reset token expired");
        }

        User user = getById(prt.getUserId());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        prt.setUsed(true);
        passwordResetTokenRepository.save(prt);

        log.info("Password reset completed for user {}", prt.getUserId());
    }

    @CacheEvict(value = "users:profile", key = "#userId")
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getById(userId);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Invalid current password attempt for user {}", userId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed for user {}", userId);
    }
}
