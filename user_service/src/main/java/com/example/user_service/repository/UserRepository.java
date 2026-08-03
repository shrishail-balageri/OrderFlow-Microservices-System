package com.example.user_service.repository;

import com.example.user_service.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<user ,Integer> {
}
 
