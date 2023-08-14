package com.silversixx.bankingapp.dao;

import com.silversixx.bankingapp.entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findUserByEmail(String email);
    Optional<UserModel> findByAccountNumber(String accountNumber);
    Optional<UserModel> findByPhoneNumber(String phoneNumber);
    @Transactional
    @Modifying
    @Query("UPDATE UserModel u " +
            "SET u.enabled = TRUE WHERE u.email = ?1")
    void enableUser(String email);
    @Transactional
    @Modifying
    @Query("UPDATE UserModel u " +
            "SET u.password = :newPassword WHERE u.email = :email")
    void changePassword(@Param("email") String email, @Param("newPassword") String newPassword);
}

