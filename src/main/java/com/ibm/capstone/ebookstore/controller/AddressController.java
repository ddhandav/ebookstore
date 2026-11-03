package com.ibm.capstone.ebookstore.controller;

import com.ibm.capstone.ebookstore.dto.AddressRequest;
import com.ibm.capstone.ebookstore.dto.ApiResponse;
import com.ibm.capstone.ebookstore.entity.Address;
import com.ibm.capstone.ebookstore.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
@Tag(name = "Delivery Addresses", description = "Manage delivery addresses for checkout")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Get user addresses", description = "Lists all saved delivery addresses for a customer")
    public ResponseEntity<ApiResponse<List<Address>>> getAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(addressService.getUserAddresses(userId)));
    }

    @PostMapping
    @Operation(summary = "Add delivery address", description = "Saves a new delivery address for checkout")
    public ResponseEntity<ApiResponse<Address>> addAddress(
            @PathVariable Long userId,
            @RequestBody AddressRequest req) {
        Address address = addressService.addAddress(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Address added successfully", address));
    }

    @DeleteMapping("/{addressId}")
    @Operation(summary = "Delete delivery address", description = "Removes a saved address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId) {
        addressService.deleteAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.ok("Address deleted successfully", null));
    }
}
