package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.AdminUserDto;
import com.vlz.laborexchange_userservice.entity.Role;
import com.vlz.laborexchange_userservice.entity.User;
import com.vlz.laborexchange_userservice.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private AdminUserService adminUserService;

    private User testUser(Long id, boolean active) {
        Role role = new Role();
        role.setRoleName("JOB_SEEKER");
        return User.builder()
                .id(id).username("user" + id).email("user" + id + "@test.com")
                .active(active).emailVerified(true).role(role).build();
    }

    @Test
    @DisplayName("getAllUsers: возвращает страницу AdminUserDto")
    void getAllUsers_ReturnsMappedPage() {
        Page<User> userPage = new PageImpl<>(List.of(testUser(1L, true), testUser(2L, false)));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);

        Page<AdminUserDto> result = adminUserService.getAllUsers(PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        assertThat(result.getContent().get(0).isActive()).isTrue();
        assertThat(result.getContent().get(1).isActive()).isFalse();
    }

    @Test
    @DisplayName("deactivate: устанавливает active=false")
    void deactivate_SetsActiveToFalse() {
        User user = testUser(1L, true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUserService.deactivate(1L);

        assertThat(user.isActive()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("activate: устанавливает active=true")
    void activate_SetsActiveToTrue() {
        User user = testUser(1L, false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        adminUserService.activate(1L);

        assertThat(user.isActive()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("deactivate: выбрасывает EntityNotFoundException если пользователь не найден")
    void deactivate_UserNotFound_Throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> adminUserService.deactivate(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteUser: вызывает deleteById для существующего пользователя")
    void deleteUser_DeletesSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);

        adminUserService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUser: выбрасывает EntityNotFoundException если пользователь не существует")
    void deleteUser_NotFound_Throws() {
        when(userRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> adminUserService.deleteUser(99L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
