package com.ibm.capstone.ebookstore.service;

import com.ibm.capstone.ebookstore.dto.CheckoutRequest;
import com.ibm.capstone.ebookstore.entity.*;
import com.ibm.capstone.ebookstore.exception.BadRequestException;
import com.ibm.capstone.ebookstore.exception.ResourceNotFoundException;
import com.ibm.capstone.ebookstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartService cartService;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository,
                        AddressRepository addressRepository,
                        UserRepository userRepository,
                        BookRepository bookRepository,
                        CartService cartService,
                        PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartService = cartService;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Order checkout(Long userId, CheckoutRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartService.getOrCreateCart(userId);
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new BadRequestException("Your shopping cart is empty");
        }

        Address address = addressRepository.findById(req.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Delivery address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new BadRequestException("Address does not belong to user");
        }

        // Calculate Subtotal & verify stock
        BigDecimal subtotal = BigDecimal.ZERO;
        int maxDeliveryDays = 2;

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            if (book.getStockQuantity() < item.getQuantity()) {
                throw new BadRequestException("Insufficient stock for book: " + book.getTitle());
            }
            subtotal = subtotal.add(item.getSubtotal());
            if (book.getTentativeDeliveryDays() > maxDeliveryDays) {
                maxDeliveryDays = book.getTentativeDeliveryDays();
            }
        }

        // Gift points redemption (100 points = $1.00)
        int pointsToRedeem = req.getRedeemPoints() != null ? req.getRedeemPoints() : 0;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (pointsToRedeem > 0) {
            if (user.getGiftPoints() < pointsToRedeem) {
                throw new BadRequestException("You only have " + user.getGiftPoints() + " gift points available");
            }
            discountAmount = BigDecimal.valueOf(pointsToRedeem).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (discountAmount.compareTo(subtotal) > 0) {
                discountAmount = subtotal;
                pointsToRedeem = subtotal.multiply(BigDecimal.valueOf(100)).intValue();
            }
            user.setGiftPoints(user.getGiftPoints() - pointsToRedeem);
            userRepository.save(user);
        }

        // Tax calculation (5%)
        BigDecimal taxableAmount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = taxableAmount.multiply(BigDecimal.valueOf(0.05)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = taxableAmount.add(taxAmount);

        // Generate Order
        String orderNumber = "ORD-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setDeliveryAddress(address);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CONFIRMED);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setPointsRedeemed(pointsToRedeem);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setTentativeDeliveryDate(LocalDate.now().plusDays(maxDeliveryDays));

        // Create Order Items and update stock
        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() - item.getQuantity());
            bookRepository.save(book);

            OrderItem orderItem = new OrderItem(book, item.getQuantity(), item.getUnitPrice(), order);
            order.getItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Process Payment
        PaymentMethod method = req.getPaymentMethod() != null ? req.getPaymentMethod() : PaymentMethod.CREDIT_CARD;
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        Payment payment = new Payment(savedOrder, method, totalAmount, transactionId, PaymentStatus.SUCCESS);
        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);

        // Clear cart after successful checkout
        cartService.clearCart(userId);

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
    }

    /**
     * Cancel order within 48 hours of purchase.
     * Restores stock and refunds redeemed gift points.
     */
    @Transactional
    public Order cancelOrder(Long userId, Long orderId) {
        Order order = getOrderById(orderId);

        if (!order.getUser().getId().equals(userId)) {
            throw new BadRequestException("Order does not belong to user");
        }

        if (!order.isEligibleForCancellation()) {
            throw new BadRequestException("Order cannot be cancelled. Cancellation is only permitted within 48 hours of purchase.");
        }

        // Restore Stock
        for (OrderItem item : order.getItems()) {
            Book book = item.getBook();
            book.setStockQuantity(book.getStockQuantity() + item.getQuantity());
            bookRepository.save(book);
        }

        // Refund Gift Points if any were redeemed
        if (order.getPointsRedeemed() > 0) {
            User user = order.getUser();
            user.setGiftPoints(user.getGiftPoints() + order.getPointsRedeemed());
            userRepository.save(user);
        }

        // Update Order Status
        order.setStatus(OrderStatus.CANCELLED);

        // Update Payment status to REFUNDED
        if (order.getPayment() != null) {
            order.getPayment().setStatus(PaymentStatus.REFUNDED);
            paymentRepository.save(order.getPayment());
        }

        return orderRepository.save(order);
    }

    /**
     * Buy It Again: Retrieves all previously ordered books for 1-click re-order.
     */
    public List<Book> getBuyItAgainBooks(Long userId) {
        return orderRepository.findOrderedBooksByUserId(userId);
    }
}
