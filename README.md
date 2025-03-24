# Online Book Store

A professional online bookstore implementation using Spring Boot 3.3.10, JWT authentication, and MySQL database.

## Features

- Book Inventory Management
- Search Functionality
- Shopping Cart System
- Multiple Payment Options (Web, USSD, Transfer)
- Purchase History
- JWT Authentication
- Unit Tests
- SOLID Principles Implementation

## Requirements

- Java 21 (LTS)
- MySQL 8.0+
- Maven 3.8+

## Setup & Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd online-bookstore
```

2. Configure MySQL:
# Ensure MySQL is running on localhost:3306
# Default credentials:
username: root
password: root
```sql
CREATE DATABASE bookstore;

```

3. Update application.properties:
- Configure your MySQL username and password
- Update JWT secret key

4. Build the project:
```bash
mvn clean install
```

5. Run the application:
```bash
mvn spring-boot:run
```

## Project Structure

```
online-bookstore/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/bookstore/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── domain/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       └── security/
│   │   └── resources/
│   └── test/
└── pom.xml
```

## API Documentation

### Authentication Endpoints
- POST /api/auth/signup - Register new user
- POST /api/auth/login - Login user

### Book Endpoints
- GET /api/books - Get all books
- GET /api/books/{id} - Get book by ID
- POST /api/books - Add new book (Admin)
- PUT /api/books/{id} - Update book (Admin)
- DELETE /api/books/{id} - Delete book (Admin)

### Cart Endpoints
- GET /api/cart - View cart
- POST /api/cart/items - Add item to cart
- DELETE /api/cart/items/{id} - Remove item from cart

### Order Endpoints
- POST /api/orders - Place order
- GET /api/orders - View order history

## Testing

Run the tests using:
```bash
mvn test
```

## Security

- JWT-based authentication
- Role-based access control
- Password encryption using BCrypt

## High-Level Design

The application follows a layered architecture:
1. Presentation Layer (Controllers)
2. Business Layer (Services)
3. Data Access Layer (Repositories)
4. Database Layer (MySQL)

Key components:
- JWT Authentication Filter
- Custom Exception Handling
- Input Validation
- Database Transaction Management

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details