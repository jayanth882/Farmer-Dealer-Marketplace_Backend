# Farmer Dealer Marketplace - Backend

## Overview

The Farmer Dealer Marketplace Backend is a RESTful API built using **Java** and **Spring Boot**. It provides secure backend services for an online agricultural marketplace where farmers can list crops for auction and dealers can place bids. The application supports role-based authentication, product management, bidding, and auction management.

## Features

* User Registration and Login
* JWT-based Authentication and Authorization
* Role-based Access (Farmer & Dealer)
* Product Listing and Management
* Real-time Auction Management
* Bid Placement and Validation
* RESTful APIs
* MySQL Database Integration
* Exception Handling and Input Validation

## Tech Stack

* Java 17
* Spring Boot
* Spring Security
* JWT Authentication
* Spring Data JPA
* MySQL
* Maven
* Docker (Optional)

## Project Structure

```
src
├── controller
├── service
├── repository
├── entity
├── dto
├── config
├── security
├── exception
└── util
```

## Prerequisites

* Java 17 or above
* Maven
* MySQL
* IDE (IntelliJ IDEA / Spring Tool Suite / VS Code)

## Database Configuration

Update the database configuration in `application.properties`.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/farmer_marketplace
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Running the Application

Clone the repository:

```bash
git clone <repository-url>
```

Navigate to the project:

```bash
cd farmer-dealer-marketplace-backend
```

Build the project:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

The backend will start on:

```
http://localhost:8080
```

## API Endpoints

### Authentication

* `POST /api/auth/register`
* `POST /api/auth/login`

### Products

* `GET /api/products`
* `POST /api/products`
* `PUT /api/products/{id}`
* `DELETE /api/products/{id}`

### Bidding

* `POST /api/bids`
* `GET /api/bids/{productId}`

### Auctions

* `GET /api/auctions`
* `PUT /api/auctions/{id}`

## Security

* JWT-based authentication
* Spring Security
* Password encryption using BCrypt
* Protected APIs with role-based authorization

## Future Enhancements

* Payment Gateway Integration
* Email Notifications
* Live Auction Updates using WebSockets
* Analytics Dashboard
* Cloud Deployment

## Author

**Jayanth Dasari**
