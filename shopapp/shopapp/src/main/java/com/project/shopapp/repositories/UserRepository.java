package com.project.shopapp.repositories;

import com.project.shopapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);// Kiem tra user co ton tai phoneNumer ko
    Optional<User> findByPhoneNumber(String phoneNumber);// trả ề 1 user hoặc null

}
