package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.dto.UserDto;
import com.vlz.laborexchange_userservice.entity.Role;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final String EMAIL = "test@example.com";
    private final Long USER_ID = 1L;

    @Test
    @DisplayName("Создание пользователя: проверка маппинга и шифрования пароля")
    void create_ShouldSaveUserWithEncodedPassword() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email(EMAIL)
                .username("testuser")
                .password("raw_password")
                .userRole("ROLE_USER")
                .build();

        Role role = new Role();
        role.setRoleName("ROLE_USER");

        when(passwordEncoder.encode("raw_password")).thenReturn("encoded_password");
        when(roleService.findByRoleName("ROLE_USER")).thenReturn(role);

        // Act
        userService.create(request);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(EMAIL, savedUser.getEmail());
        assertEquals("encoded_password", savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole().getRoleName());
    }

    @Test
    @DisplayName("Проверка логина: пароль должен кодироваться перед проверкой")
    void checkLogin_ShouldEncodePasswordBeforeExistsCall() {
        // Arrange
        String rawPassword = "pass";
        String encodedPassword = "encoded_pass";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.existsByEmailAndPassword(EMAIL, encodedPassword)).thenReturn(true);

        // Act
        boolean result = userService.checkLogin(EMAIL, rawPassword);

        // Assert
        assertTrue(result);
        verify(userRepository).existsByEmailAndPassword(EMAIL, encodedPassword);
    }

    @Test
    @DisplayName("Получение профиля: успех")
    void getUserProfile_ShouldReturnCorrectDto() {
        // Arrange
        Role role = new Role();
        role.setRoleName("ROLE_ADMIN");

        User user = User.builder()
                .id(USER_ID)
                .email(EMAIL)
                .username("admin_nick")
                .role(role)
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        // Act
        UserDto profile = userService.getUserProfile(USER_ID);

        // Assert
        assertNotNull(profile);
        assertEquals(EMAIL, profile.getEmail());
        assertEquals("ROLE_ADMIN", profile.getRoleName());
    }

    @Test
    @DisplayName("Получение профиля: пользователь не найден — выброс исключения")
    void getById_NotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> userService.getById(USER_ID));
    }

    @Test
    @DisplayName("Обновление пользователя: данные должны меняться")
    void update_ShouldChangeFieldsAndSave() {
        // Arrange
        User existingUser = User.builder()
                .id(USER_ID)
                .username("old_name")
                .email("old@mail.com")
                .build();

        UserDto updateDto = UserDto.builder()
                .id(USER_ID)
                .username("new_name")
                .email("new@mail.com")
                .build();

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User updatedUser = userService.update(updateDto);

        // Assert
        assertEquals("new_name", updatedUser.getUsername());
        assertEquals("new@mail.com", updatedUser.getEmail());
        verify(userRepository).save(existingUser);
    }
}