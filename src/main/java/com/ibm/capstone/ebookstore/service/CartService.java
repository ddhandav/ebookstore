package com.ibm.capstone.ebookstore.service;

import com.ibm.capstone.ebookstore.dto.AddToCartRequest;
import com.ibm.capstone.ebookstore.dto.UpdateCartItemRequest;
import com.ibm.capstone.ebookstore.entity.Book;
import com.ibm.capstone.ebookstore.entity.Cart;
import com.ibm.capstone.ebookstore.entity.CartItem;
import com.ibm.capstone.ebookstore.entity.User;
import com.ibm.capstone.ebookstore.exception.BadRequestException;
import com.ibm.capstone.ebookstore.exception.ResourceNotFoundException;
import com.ibm.capstone.ebookstore.repository.BookRepository;
import com.ibm.capstone.ebookstore.repository.CartItemRepository;
import com.ibm.capstone.ebookstore.repository.CartRepository;
import com.ibm.capstone.ebookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        });
    }

    @Transactional
    public Cart addToCart(Long userId, AddToCartRequest req) {
        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        Cart cart = getOrCreateCart(userId);
        Book book = bookRepository.findById(req.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + req.getBookId()));

        if (book.getStockQuantity() < req.getQuantity()) {
            throw new BadRequestException("Requested quantity exceeds available stock (" + book.getStockQuantity() + ")");
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndBookId(cart.getId(), book.getId());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + req.getQuantity());
            item.setUnitPrice(book.getPrice());
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(book, req.getQuantity(), book.getPrice(), cart);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Transactional
    public Cart updateCartItem(Long userId, Long itemId, UpdateCartItemRequest req) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
        }

        if (req.getQuantity() == null || req.getQuantity() <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            if (item.getBook().getStockQuantity() < req.getQuantity()) {
                throw new BadRequestException("Requested quantity exceeds available stock");
            }
            item.setQuantity(req.getQuantity());
            cartItemRepository.save(item);
        }

        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Transactional
    public Cart removeCartItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to user's cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return cartRepository.findById(cart.getId()).orElse(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}
