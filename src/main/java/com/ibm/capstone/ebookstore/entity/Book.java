package com.ibm.capstone.ebookstore.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher; // Brand

    @Column(nullable = false)
    private Integer stockQuantity = 100;

    private String coverImageUrl;

    @Column(nullable = false)
    private Integer tentativeDeliveryDays = 2; // e.g. 2 business days

    private Double rating = 4.5;

    public Book() {}

    public Book(String title, String author, String isbn, String description, BigDecimal price, Category category, Publisher publisher, Integer stockQuantity, String coverImageUrl, Integer tentativeDeliveryDays, Double rating) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.price = price;
        this.category = category;
        this.publisher = publisher;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 100;
        this.coverImageUrl = coverImageUrl;
        this.tentativeDeliveryDays = tentativeDeliveryDays != null ? tentativeDeliveryDays : 2;
        this.rating = rating != null ? rating : 4.5;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Publisher getPublisher() { return publisher; }
    public void setPublisher(Publisher publisher) { this.publisher = publisher; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Integer getTentativeDeliveryDays() { return tentativeDeliveryDays; }
    public void setTentativeDeliveryDays(Integer tentativeDeliveryDays) { this.tentativeDeliveryDays = tentativeDeliveryDays; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
}
