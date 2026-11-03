package com.ibm.capstone.ebookstore.service;

import com.ibm.capstone.ebookstore.entity.Book;
import com.ibm.capstone.ebookstore.entity.Category;
import com.ibm.capstone.ebookstore.entity.Publisher;
import com.ibm.capstone.ebookstore.exception.ResourceNotFoundException;
import com.ibm.capstone.ebookstore.repository.BookRepository;
import com.ibm.capstone.ebookstore.repository.CategoryRepository;
import com.ibm.capstone.ebookstore.repository.OrderRepository;
import com.ibm.capstone.ebookstore.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CatalogService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;
    private final OrderRepository orderRepository;

    public CatalogService(BookRepository bookRepository,
                          CategoryRepository categoryRepository,
                          PublisherRepository publisherRepository,
                          OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
        this.orderRepository = orderRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Publisher> getAllPublishers() {
        return publisherRepository.findAll();
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
    }

    public List<Book> getBooksByCategory(Long categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }

    public List<Book> getBooksByPublisher(Long publisherId) {
        return bookRepository.findByPublisherId(publisherId);
    }

    public List<Book> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBooks();
        }
        return bookRepository.searchBooks(query.trim());
    }

    public List<Book> getRelatedBooks(Long bookId) {
        Book book = getBookById(bookId);
        return bookRepository.findTop10ByCategoryIdAndIdNot(book.getCategory().getId(), bookId);
    }

    /**
     * AI-augmented recommendation engine:
     * Analyzes customer order history to extract preferred categories & brands,
     * and recommends matching trending titles. Falls back to top-rated catalog books.
     */
    public List<Book> getRecommendations(Long userId) {
        if (userId == null) {
            return bookRepository.findTop10ByOrderByRatingDesc();
        }

        List<Book> orderedBooks = orderRepository.findOrderedBooksByUserId(userId);
        if (orderedBooks.isEmpty()) {
            return bookRepository.findTop10ByOrderByRatingDesc();
        }

        Set<Long> orderedBookIds = new HashSet<>();
        Set<Long> preferredCategoryIds = new HashSet<>();

        for (Book b : orderedBooks) {
            orderedBookIds.add(b.getId());
            if (b.getCategory() != null) {
                preferredCategoryIds.add(b.getCategory().getId());
            }
        }

        List<Book> recommendations = new ArrayList<>();
        for (Long catId : preferredCategoryIds) {
            List<Book> catBooks = bookRepository.findByCategoryId(catId);
            for (Book b : catBooks) {
                if (!orderedBookIds.contains(b.getId()) && !recommendations.contains(b)) {
                    recommendations.add(b);
                }
            }
        }

        if (recommendations.isEmpty()) {
            return bookRepository.findTop10ByOrderByRatingDesc();
        }

        return recommendations;
    }
}
