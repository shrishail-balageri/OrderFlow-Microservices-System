package com.example.user_service.service;

import com.example.user_service.entity.user;
import com.example.user_service.repository.UserRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;
     public UserService(UserRepository repo){
        this.repo=repo;
     }
     public user save(user user){
        return repo.save(user);
     }
     public List<user> getAll(){

      return repo.findAll();
     }
}