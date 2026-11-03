# E-Bookstore Platform Backend API

> **AI Specialist - Cloud FullStack Capstone Project**  
> *Built with IBM BOB Agentic IDE*

---

## 📖 Project Overview

The **E-Bookstore Platform** is a scalable, cloud-native backend solution implementing the end-to-end customer journey for an online book eCommerce store.

---

## 🎯 Implemented Customer Journeys (Slides 3–11)

| # | Customer Journey Step | Description & Implemented Feature | API Endpoint |
|---|------------------------|------------------------------------|--------------|
| **1** | **User Registration & Login** | User authentication with initial **500 Welcome Bonus Gift Points** | `POST /api/auth/register`<br>`POST /api/auth/login` |
| **2** | **Category Selection** | Browse categories (Technology, Fiction, Business, Science, etc.) | `GET /api/catalog/categories` |
| **3** | **Brand / Publisher Browsing** | Filter books by publishers (O'Reilly, Penguin, HarperCollins, Pearson, Packt) | `GET /api/catalog/brands` |
| **4** | **Product Catalogue & Search** | Access catalog, search by keyword/author, filter by category & brand | `GET /api/catalog/books` |
| **5** | **Product Details & Delivery Date** | Detailed product information tagged with **tentative delivery days/dates** | `GET /api/catalog/books/{id}` |
| **6** | **Related Products** | Automatic recommendation of related books in the same category | `GET /api/catalog/books/{id}/related` |
| **7** | **Shopping Basket / Cart** | Add to basket, update item quantities, remove item, subtotal calculation | `GET /api/cart/{userId}`<br>`POST /api/cart/{userId}/items`<br>`PUT /api/cart/{userId}/items/{id}` |
| **8** | **Delivery Address Selection** | Add, list, and select delivery address for order checkout | `GET /api/users/{userId}/addresses`<br>`POST /api/users/{userId}/addresses` |
| **9** | **Payment & Points Redemption** | Checkout with Credit Card / Debit Card + **Gift points redemption** (100 pts = $1 discount) | `POST /api/orders/checkout/{userId}` |
| **10** | **Purchase Confirmation** | Instant order creation with unique Order Number (`ORD-YYYYMMDD-XXXX`), payment confirmation | `GET /api/orders/{orderId}`<br>`GET /api/orders/number/{orderNumber}` |
| **11** | **Cancel Order (< 48 hrs)** | Customer can cancel order within 48 hours of purchase. Restores book stock & refunds gift points | `POST /api/orders/{orderId}/cancel/{userId}` |
| **12** | **Buy It Again** | 1-click retrieval of previously purchased products from order history | `GET /api/orders/buy-it-again/{userId}` |
| **13** | **AI-Augmented Recommendations** | Recommends books based on user purchase history and preferred genres | `GET /api/catalog/recommendations?userId={id}` |

---

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.3.4 (Java 21)
- **Data Persistence**: Spring Data JPA & Hibernate
- **Databases**:
  - **H2 In-Memory** (default for local zero-config run)
  - **PostgreSQL** (production profile via `application-postgres.properties`)
- **API Documentation**: OpenAPI 3.0 / Swagger UI (`springdoc-openapi-starter-webmvc-ui:2.6.0`)
- **Testing**: JUnit 5, MockMvc, Spring Boot Test

---

## 🚀 Quick Start Guide (Slide 12)

### Prerequisites
- Java 21 JDK installed
- Maven 3.9+ installed

### Step 1: Clone / Navigate to Project Directory
```bash
cd ebookstore-api
```

### Step 2: Build & Run Tests
```bash
mvn clean test
```

### Step 3: Run the Application
```bash
mvn spring-boot:run
```

The application starts on port `8080`.

### Step 4: Access Swagger UI & OpenAPI Specification
- **Interactive Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:ebookstoredb`, User: `sa`, Password: empty)

---

## 🗄️ PostgreSQL Configuration (Optional)

To run with a local or cloud PostgreSQL instance:

1. Update credentials in `src/main/resources/application-postgres.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ebookstoredb
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```
2. Start the application with the PostgreSQL profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=postgres
   ```

---

## 🧪 Sample API Endpoints & Testing

### 1. Register User (with 500 Gift Points)
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Smith","email":"alice@example.com","password":"password123"}'
```

### 2. Browse Books Catalog
```bash
curl http://localhost:8080/api/catalog/books
```

### 3. Add to Basket
```bash
curl -X POST http://localhost:8080/api/cart/1/items \
  -H "Content-Type: application/json" \
  -d '{"bookId":1,"quantity":2}'
```

### 4. Checkout with Gift Points Discount
```bash
curl -X POST http://localhost:8080/api/orders/checkout/1 \
  -H "Content-Type: application/json" \
  -d '{
    "addressId": 1,
    "paymentMethod": "CREDIT_CARD",
    "cardNumber": "4111222233334444",
    "cardExpiry": "12/28",
    "cvv": "123",
    "redeemPoints": 200
  }'
```

### 5. Cancel Order within 48 Hours
```bash
curl -X POST http://localhost:8080/api/orders/1/cancel/1
```

### 6. Buy It Again
```bash
curl http://localhost:8080/api/orders/buy-it-again/1
```

---

## 📦 Project File Structure

```
ebookstore-api/
├── pom.xml
├── openapi.yaml
├── README.md
└── src/
    ├── main/
    │   ├── java/com/ibm/capstone/ebookstore/
    │   │   ├── EBookstoreApplication.java
    │   │   ├── config/
    │   │   │   ├── OpenApiConfig.java
    │   │   │   └── DataInitializer.java
    │   │   ├── controller/
    │   │   │   ├── AuthController.java
    │   │   │   ├── CatalogController.java
    │   │   │   ├── AddressController.java
    │   │   │   ├── CartController.java
    │   │   │   └── OrderController.java
    │   │   ├── dto/
    │   │   │   ├── ApiResponse.java
    │   │   │   ├── AuthResponse.java
    │   │   │   ├── LoginRequest.java
    │   │   │   ├── RegisterRequest.java
    │   │   │   ├── AddressRequest.java
    │   │   │   ├── AddToCartRequest.java
    │   │   │   ├── UpdateCartItemRequest.java
    │   │   │   └── CheckoutRequest.java
    │   │   ├── entity/
    │   │   │   ├── User.java
    │   │   │   ├── Address.java
    │   │   │   ├── Category.java
    │   │   │   ├── Publisher.java
    │   │   │   ├── Book.java
    │   │   │   ├── Cart.java
    │   │   │   ├── CartItem.java
    │   │   │   ├── Order.java
    │   │   │   ├── OrderItem.java
    │   │   │   ├── Payment.java
    │   │   │   ├── OrderStatus.java
    │   │   │   ├── PaymentMethod.java
    │   │   │   └── PaymentStatus.java
    │   │   ├── exception/
    │   │   │   ├── BadRequestException.java
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   └── GlobalExceptionHandler.java
    │   │   ├── repository/
    │   │   │   ├── UserRepository.java
    │   │   │   ├── AddressRepository.java
    │   │   │   ├── CategoryRepository.java
    │   │   │   ├── PublisherRepository.java
    │   │   │   ├── BookRepository.java
    │   │   │   ├── CartRepository.java
    │   │   │   ├── CartItemRepository.java
    │   │   │   ├── OrderRepository.java
    │   │   │   ├── OrderItemRepository.java
    │   │   │   └── PaymentRepository.java
    │   │   └── service/
    │   │       ├── AuthService.java
    │   │       ├── AddressService.java
    │   │       ├── CatalogService.java
    │   │       ├── CartService.java
    │   │       └── OrderService.java
    │   └── resources/
    │       ├── application.properties
    │       └── application-postgres.properties
    └── test/
        └── java/com/ibm/capstone/ebookstore/
            └── EBookstoreApplicationTests.java
```
