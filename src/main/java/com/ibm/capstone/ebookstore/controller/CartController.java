package com.ibm.capstone.ebookstore.controller;

import com.ibm.capstone.ebookstore.dto.AddToCartRequest;
import com.ibm.capstone.ebookstore.dto.ApiResponse;
import com.ibm.capstone.ebookstore.dto.UpdateCartItemRequest;
import com.ibm.capstone.ebookstore.entity.Cart;
import com.ibm.capstone.ebookstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart/{userId}")
@Tag(name = "Shopping Cart", description = "Shopping basket operations: view, add, update, remove items, clear")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get user shopping cart", description = "Retrieves cart items, item counts, and calculated subtotal")
    public ResponseEntity<ApiResponse<Cart>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(cartService.getOrCreateCart(userId)));
    }

    @PostMapping("/items")
    @Operation(summary = "Add book to cart", description = "Adds a book product to the user's shopping basket")
    public ResponseEntity<ApiResponse<Cart>> addItem(
            @PathVariable Long userId,
            @RequestBody AddToCartRequest req) {
        Cart cart = cartService.addToCart(userId, req);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity", description = "Updates quantity of a specific item in the basket")
    public ResponseEntity<ApiResponse<Cart>> updateItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest req) {
        Cart cart = cartService.updateCartItem(userId, itemId, req);
        return ResponseEntity.ok(ApiResponse.ok("Cart item updated", cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart", description = "Deletes a specific item from the shopping basket")
    public ResponseEntity<ApiResponse<Cart>> removeItem(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        Cart cart = cartService.removeCartItem(userId, itemId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear shopping cart", description = "Removes all items from the shopping basket")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", null));
    }
}
