package com.project.shopapp.repositories;

import com.project.shopapp.models.Order;
import com.project.shopapp.responses.OrderResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Tim các đơn hàng của 11 user nào đó
    List<Order> findByUserId(long Id);
}
