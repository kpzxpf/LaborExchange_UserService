package com.vlz.laborexchange_userservice.repository;

import com.vlz.laborexchange_userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndPassword(String email, String password);

    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    String getEmailById(@Param("userId") Long userId);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Long getUserIdByEmail(@Param("email") String email);
}