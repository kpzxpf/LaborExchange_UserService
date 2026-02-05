package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.entity.Role;
import com.vlz.laborexchange_userservice.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private final String ROLE_NAME = "ROLE_USER";
    private final String EMAIL = "test@example.com";
    private final Long USER_ID = 1L;

    @Test
    @DisplayName("findByRoleName: успех — возвращает роль")
    void findByRoleName_Success() {
        // Arrange
        Role role = new Role();
        role.setRoleName(ROLE_NAME);
        when(roleRepository.findByRoleName(ROLE_NAME)).thenReturn(Optional.of(role));

        // Act
        Role result = roleService.findByRoleName(ROLE_NAME);

        // Assert
        assertEquals(ROLE_NAME, result.getRoleName());
        verify(roleRepository).findByRoleName(ROLE_NAME);
    }

    @Test
    @DisplayName("findByRoleName: ошибка — роль не найдена")
    void findByRoleName_NotFound_ThrowsException() {
        // Arrange
        when(roleRepository.findByRoleName(ROLE_NAME)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> roleService.findByRoleName(ROLE_NAME));

        assertTrue(exception.getMessage().contains(ROLE_NAME));
    }

    @Test
    @DisplayName("getUserRoleByEmail: успех")
    void getUserRoleByEmail_Success() {
        // Arrange
        when(roleRepository.findRoleNameByUserEmail(EMAIL)).thenReturn(Optional.of(ROLE_NAME));

        // Act
        String result = roleService.getUserRoleByEmail(EMAIL);

        // Assert
        assertEquals(ROLE_NAME, result);
    }

    @Test
    @DisplayName("getUserRoleById: успех")
    void getUserRoleById_Success() {
        // Arrange
        when(roleRepository.findRoleNameByUserId(USER_ID)).thenReturn(Optional.of(ROLE_NAME));

        // Act
        String result = roleService.getUserRoleById(USER_ID);

        // Assert
        assertEquals(ROLE_NAME, result);
    }
}