# BookSphere Library Management System

A modern library management system built with Spring Boot, featuring user management, book catalog, and transaction handling.

## Features

- User Management with Role-based Access Control
- Book Catalog Management
- Genre Classification
- Transaction Management (Issue/Return)
- Notification System
- H2 Database Integration
- RESTful API Endpoints

## Tech Stack

- Java 21
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- H2 Database
- Maven
- Lombok

## Prerequisites

- JDK 21 or later
- Maven 3.9.6 or later

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/ArpanChaudhary/BookSphereLibrary.git
cd BookSphereLibrary
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the application:
- Main Application: http://localhost:5000
- H2 Console: http://localhost:5000/h2-console
  - JDBC URL: jdbc:h2:mem:testdb
  - Username: sa
  - Password: (empty)

## Default Admin Credentials

- Username: admin
- Password: admin

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── booksphere/
│   │           ├── config/
│   │           ├── controller/
│   │           ├── model/
│   │           ├── repository/
│   │           ├── service/
│   │           └── BookSphereApplication.java
│   └── resources/
│       ├── static/
│       ├── templates/
│       ├── application.properties
│       ├── data.sql
│       └── schema.sql
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 