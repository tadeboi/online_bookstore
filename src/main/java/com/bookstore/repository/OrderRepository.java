package com.bookstore.repository;

import com.bookstore.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
}