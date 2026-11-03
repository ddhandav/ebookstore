package com.ibm.capstone.ebookstore.service;

import com.ibm.capstone.ebookstore.dto.AuthResponse;
import com.ibm.capstone.ebookstore.dto.LoginRequest;
import com.ibm.capstone.ebookstore.dto.RegisterRequest;
import com.ibm.capstone.ebookstore.entity.Cart;
import com.ibm.capstone.ebookstore.entity.User;
import com.ibm.capstone.ebookstore.exception.BadRequestException;
import com.ibm.capstone.ebookstore.exception.ResourceNotFoundException;
import com.ibm.capstone.ebookstore.repository.CartRepository;
import com.ibm.capstone.ebookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public AuthService(UserRepository userRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (req.getEmail() == null || req.getEmail().trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (req.getPassword() == null || req.getPassword().length() < 4) {
            throw new BadRequestException("Password must be at least 4 characters");
        }
        if (userRepository.existsByEmail(req.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User(req.getName(), req.getEmail().trim().toLowerCase(), req.getPassword(), 500);
        User saved = userRepository.save(user);

        // Auto create empty cart for new user
        Cart cart = new Cart(saved);
        cartRepository.save(cart);

        return new AuthResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getGiftPoints(), "jwt-token-simulated-" + saved.getId(), "Registration successful! You received 500 welcome bonus gift points.");
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getPassword().equals(req.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        return new AuthResponse(user.getId(), user.getName(), user.getEmail(), user.getGiftPoints(), "jwt-token-simulated-" + user.getId(), "Login successful");
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
}
