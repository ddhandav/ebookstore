package com.ibm.capstone.ebookstore.controller;

import com.ibm.capstone.ebookstore.dto.ApiResponse;
import com.ibm.capstone.ebookstore.entity.Book;
import com.ibm.capstone.ebookstore.entity.Category;
import com.ibm.capstone.ebookstore.entity.Publisher;
import com.ibm.capstone.ebookstore.service.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@Tag(name = "Product Catalog", description = "Book browse, search, categories, brands, and recommendations APIs")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieves the list of book categories (Fiction, Tech, etc.)")
    public ResponseEntity<ApiResponse<List<Category>>> getCategories() {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getAllCategories()));
    }

    @GetMapping("/brands")
    @Operation(summary = "Get all publisher brands", description = "Retrieves publisher brands (Penguin, O'Reilly, HarperCollins, etc.)")
    public ResponseEntity<ApiResponse<List<Publisher>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getAllPublishers()));
    }

    @GetMapping("/books")
    @Operation(summary = "Browse or search books", description = "Filter books by category, brand/publisher, or search keyword")
    public ResponseEntity<ApiResponse<List<Book>>> getBooks(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String search) {

        List<Book> books;
        if (search != null && !search.trim().isEmpty()) {
            books = catalogService.searchBooks(search);
        } else if (categoryId != null && brandId != null) {
            books = catalogService.getAllBooks().stream()
                    .filter(b -> b.getCategory().getId().equals(categoryId) && b.getPublisher().getId().equals(brandId))
                    .toList();
        } else if (categoryId != null) {
            books = catalogService.getBooksByCategory(categoryId);
        } else if (brandId != null) {
            books = catalogService.getBooksByPublisher(brandId);
        } else {
            books = catalogService.getAllBooks();
        }

        return ResponseEntity.ok(ApiResponse.ok(books));
    }

    @GetMapping("/books/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves detailed book product information including tentative delivery date")
    public ResponseEntity<ApiResponse<Book>> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getBookById(id)));
    }

    @GetMapping("/books/{id}/related")
    @Operation(summary = "Get related books", description = "Retrieves related books in the same category for selection")
    public ResponseEntity<ApiResponse<List<Book>>> getRelatedBooks(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getRelatedBooks(id)));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get AI recommendations", description = "Recommends books based on user order history and preferences")
    public ResponseEntity<ApiResponse<List<Book>>> getRecommendations(@RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(ApiResponse.ok(catalogService.getRecommendations(userId)));
    }
}
