package com.vlz.laborexchange_userservice.repository;

import com.vlz.laborexchange_userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    @Query("SELECT u.username FROM User u WHERE u.id = :userId")
    String findUsernameById(@Param("userId") Long userId);

    boolean existsByEmailAndPassword(String email, String password);
    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    String getEmailById(@Param("userId") Long userId);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long getUserIdByEmail(@Param("email") String email);
}