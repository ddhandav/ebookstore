package com.ibm.capstone.ebookstore.controller;

import com.ibm.capstone.ebookstore.dto.ApiResponse;
import com.ibm.capstone.ebookstore.dto.CheckoutRequest;
import com.ibm.capstone.ebookstore.entity.Book;
import com.ibm.capstone.ebookstore.entity.Order;
import com.ibm.capstone.ebookstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders & Checkout", description = "Checkout, order processing, purchase confirmation, order history, cancel within 48h, and Buy It Again")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout/{userId}")
    @Operation(summary = "Checkout and place order", description = "Executes checkout, selects address, applies gift point discounts, processes payment, and confirms purchase")
    public ResponseEntity<ApiResponse<Order>> checkout(
            @PathVariable Long userId,
            @RequestBody CheckoutRequest req) {
        Order order = orderService.checkout(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Order placed successfully. Thank you for your purchase!", order));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user order history", description = "Retrieves all past orders placed by the customer")
    public ResponseEntity<ApiResponse<List<Order>>> getOrderHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getUserOrders(userId)));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get order by ID", description = "Retrieves order and item details by order ID")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderById(orderId)));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number", description = "Retrieves order confirmation by tracking order number (e.g. ORD-20261102-XXXX)")
    public ResponseEntity<ApiResponse<Order>> getOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getOrderByNumber(orderNumber)));
    }

    @PostMapping("/{orderId}/cancel/{userId}")
    @Operation(summary = "Cancel order within 48 hours", description = "Cancels an eligible order within 48 hours of placement, restores stock, and refunds redeemed gift points")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @PathVariable Long orderId,
            @PathVariable Long userId) {
        Order cancelledOrder = orderService.cancelOrder(userId, orderId);
        return ResponseEntity.ok(ApiResponse.ok("Order cancelled successfully. Any redeemed gift points have been refunded.", cancelledOrder));
    }

    @GetMapping("/buy-it-again/{userId}")
    @Operation(summary = "Buy It Again items", description = "Retrieves unique items previously purchased by customer for quick re-order")
    public ResponseEntity<ApiResponse<List<Book>>> getBuyItAgain(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(orderService.getBuyItAgainBooks(userId)));
    }
}
