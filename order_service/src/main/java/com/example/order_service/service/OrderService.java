package com.example.order_service.service;

import com.example.order_service.entity.Orders;
import com.example.order_service.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class OrderService{
    private final OrderRepository repo;

    public OrderService(OrderRepository repo){
        this.repo=repo;
    }
    public Orders save(Orders Order){
        return repo.save(Order);
    }
    public List<Orders> getAll(){
        return repo.findAll();
    }
}