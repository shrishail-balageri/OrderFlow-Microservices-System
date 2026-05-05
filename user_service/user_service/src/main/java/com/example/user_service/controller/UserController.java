package com.example.user_service.controller;
import com.example.user_service.entity.user;
import com.example.user_service.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/users")
public class UserController{
    private final UserService service;
    
    public UserController(UserService service){
        this.service=service;
    }
    @PostMapping
    public user create(@RequestBody user User){
        return service.save(User);
    }
    @GetMapping
    public List<user> getAll(){
        return service.getAll();
    }
 }