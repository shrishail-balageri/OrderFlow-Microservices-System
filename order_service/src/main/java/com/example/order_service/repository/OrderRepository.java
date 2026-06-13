package com.example.order_service.repository;

import com.example.order_service.entity.Orders;
import org.springframework.data.jpa.repository.*;

public interface OrderRepository extends JpaRepository<Orders,Long>{

}