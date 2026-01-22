package com.vlz.laborexchange_userservice.repository;

import com.vlz.laborexchange_userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndPassword(String email, String password);

    String getEmailById(Long userId);
}
