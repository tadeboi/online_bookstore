package com.bookstore.repository;

import com.bookstore.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Example of a custom query
    @Query("SELECT o FROM Order o WHERE o.user.id = ?1 AND o.status = 'PENDING'")
    List<Order> findPendingOrdersByUserId(Long userId);
    
    // Example of a native SQL query
    @Query(value = "SELECT * FROM orders o WHERE o.user_id = ?1 AND YEAR(o.order_date) = ?2", 
           nativeQuery = true)
    List<Order> findOrdersByUserAndYear(Long userId, Integer year);
}