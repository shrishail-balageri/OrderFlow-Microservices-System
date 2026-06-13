package com.example.order_service.controller;

import com.example.order_service.entity.Orders;
import com.example.order_service.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/orders")
public class OrderController{
     private final OrderService service;

     public OrderController(OrderService service){
        this.service=service;
     }
     @PostMapping
     public Orders create(@RequestBody Orders Order){
        return service.save(Order);
     } 
     @GetMapping
     public List<Orders> getAll(){
        return service.getAll();
     }
     
}