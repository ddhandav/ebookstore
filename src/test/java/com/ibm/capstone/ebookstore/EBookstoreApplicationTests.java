package com.ibm.capstone.ebookstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.capstone.ebookstore.dto.AddToCartRequest;
import com.ibm.capstone.ebookstore.dto.CheckoutRequest;
import com.ibm.capstone.ebookstore.dto.LoginRequest;
import com.ibm.capstone.ebookstore.dto.RegisterRequest;
import com.ibm.capstone.ebookstore.entity.Address;
import com.ibm.capstone.ebookstore.entity.Book;
import com.ibm.capstone.ebookstore.entity.PaymentMethod;
import com.ibm.capstone.ebookstore.entity.User;
import com.ibm.capstone.ebookstore.repository.AddressRepository;
import com.ibm.capstone.ebookstore.repository.BookRepository;
import com.ibm.capstone.ebookstore.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EBookstoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Test
    @DisplayName("Test 1: Browse Catalog Categories and Brands")
    void testGetCategoriesAndBrands() throws Exception {
        mockMvc.perform(get("/api/catalog/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(4))));

        mockMvc.perform(get("/api/catalog/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(5))));
    }

    @Test
    @DisplayName("Test 2: Search Books by Keyword and Filter by Category")
    void testSearchAndFilterBooks() throws Exception {
        mockMvc.perform(get("/api/catalog/books").param("search", "Clean Code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data[0].title", containsString("Clean Code")));

        mockMvc.perform(get("/api/catalog/books").param("categoryId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Test 3: Get Book Detail, Tentative Delivery Days, and Related Books")
    void testBookDetailsAndRelated() throws Exception {
        Book book = bookRepository.findAll().get(0);

        mockMvc.perform(get("/api/catalog/books/" + book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is(book.getTitle())))
                .andExpect(jsonPath("$.data.tentativeDeliveryDays", notNullValue()));

        mockMvc.perform(get("/api/catalog/books/" + book.getId() + "/related"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", is(notNullValue())));
    }

    @Test
    @DisplayName("Test 4: User Registration, Bonus Gift Points, and Login")
    void testUserAuthFlow() throws Exception {
        String email = "testuser" + System.currentTimeMillis() + "@example.com";
        RegisterRequest registerReq = new RegisterRequest("Test User", email, "secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.giftPoints", is(500)));

        LoginRequest loginReq = new LoginRequest(email, "secret123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is(email)));
    }

    @Test
    @DisplayName("Test 5: Full Customer Journey - Add to Cart, Checkout with Gift Points, Cancel within 48h, and Buy It Again")
    void testFullCustomerJourney() throws Exception {
        // 1. Get seed user and address
        User user = userRepository.findByEmail("john.doe@example.com").orElseThrow();
        Address address = addressRepository.findByUserId(user.getId()).get(0);
        Book book = bookRepository.findAll().get(0);

        // 2. Add item to cart
        AddToCartRequest addReq = new AddToCartRequest(book.getId(), 2);
        mockMvc.perform(post("/api/cart/" + user.getId() + "/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.items", hasSize(greaterThanOrEqualTo(1))));

        // 3. View cart
        mockMvc.perform(get("/api/cart/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalQuantity", greaterThanOrEqualTo(2)));

        // 4. Checkout with Gift Points Redemption (Redeem 100 points = $1.00 discount)
        CheckoutRequest checkoutReq = new CheckoutRequest();
        checkoutReq.setAddressId(address.getId());
        checkoutReq.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        checkoutReq.setCardNumber("4111222233334444");
        checkoutReq.setCardExpiry("12/28");
        checkoutReq.setCvv("123");
        checkoutReq.setRedeemPoints(100);

        String orderResponse = mockMvc.perform(post("/api/orders/checkout/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.orderNumber", startsWith("ORD-")))
                .andExpect(jsonPath("$.data.pointsRedeemed", is(100)))
                .andExpect(jsonPath("$.data.status", is("CONFIRMED")))
                .andExpect(jsonPath("$.data.payment.status", is("SUCCESS")))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(orderResponse).path("data").path("id").asLong();

        // 5. Verify order history
        mockMvc.perform(get("/api/orders/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        // 6. Buy It Again API
        mockMvc.perform(get("/api/orders/buy-it-again/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        // 7. AI Recommendations API based on order history
        mockMvc.perform(get("/api/catalog/recommendations").param("userId", user.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));

        // 8. Cancel order within 48 hours
        mockMvc.perform(post("/api/orders/" + orderId + "/cancel/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("CANCELLED")))
                .andExpect(jsonPath("$.data.payment.status", is("REFUNDED")));
    }
}
