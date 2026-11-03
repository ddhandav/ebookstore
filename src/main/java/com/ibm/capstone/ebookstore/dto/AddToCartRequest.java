package com.ibm.capstone.ebookstore.dto;

public class AddToCartRequest {
    private Long bookId;
    private Integer quantity = 1;

    public AddToCartRequest() {}
    public AddToCartRequest(Long bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
