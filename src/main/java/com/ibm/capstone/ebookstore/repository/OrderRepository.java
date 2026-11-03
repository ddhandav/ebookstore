package com.ibm.capstone.ebookstore.repository;

import com.ibm.capstone.ebookstore.entity.Book;
import com.ibm.capstone.ebookstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT DISTINCT oi.book FROM Order o JOIN o.items oi WHERE o.user.id = :userId ORDER BY oi.book.rating DESC")
    List<Book> findOrderedBooksByUserId(@Param("userId") Long userId);
}
