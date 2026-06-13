package com.example.order_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Orders{
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Long id;

    private String product;
    private int quantity;

}