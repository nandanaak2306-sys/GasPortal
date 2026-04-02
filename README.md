# 🏆 GasPortal - Online Gas Cylinder Booking Platform

![GasPortal Logo](file:///C:/Users/nanda/.gemini/antigravity/brain/90cf4292-95ed-4135-9490-f0b353c8cdff/gas_portal_logo_1775145160267.png)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.10-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-darkgreen.svg)](https://www.thymeleaf.org/)

**GasPortal** is a robust, end-to-end web application designed to streamline the gas cylinder booking process for consumers and provide powerful inventory management for agencies and suppliers. Built with high-performance Java technologies, it ensures reliability, security, and a seamless user experience.

---

## 🌟 Key Features

### 👤 User Capabilities
- **Hassle-Free Registration**: Quick and secure onboarding for new users.
- **Interactive Dashboard**: Track booking history, current status, and manage profile details.
- **Instant Booking**: Real-time gas cylinder booking with immediate confirmation.

### 🏢 Agency Management
- **Centralized Dashboard**: Manage orders, track registrations, and monitor agency performance.
- **Inventory Control**: Real-time stock validation to prevent over-ordering and ensure supply.
- **Supplier Integration**: Seamlessly connect with suppliers to restock inventory.

### 🛠️ Core Functionality
- **Payment Integration**: Secure payment gateway for upfront bookings.
- **Order Tracking**: Comprehensive order lifecycle management from placement to delivery.
- **Stock Alerts**: Automated notifications for low-stock thresholds.

---

## 💻 Tech Stack

- **Backend**: Spring Boot, Spring Data JPA, Java 17
- **Frontend**: Thymeleaf Templates, CSS3, HTML5
- **Database**: MySQL (optimized for high concurrency)
- **Security**: Form-based authentication and role-based access control.
- **Build Tool**: Maven

---

## 🚀 Getting Started

### Prerequisites

- **Java Development Kit (JDK)**: 17 or higher.
- **MySQL Server**: 8.0 or higher.
- **Maven**: Latest version.

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/nandanaak2306-sys/GasPortal.git
   cd GasPortal
   ```

2. **Configure Database**:
   - Create a database named `gasportal` in MySQL.
   - Update `src/main/resources/application.properties` with your database credentials.
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/gasportal
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and Run**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the App**:
   Navigate to `http://localhost:8080` in your browser.

---

## 📸 Application Mockup

![GasPortal Mockup](file:///C:/Users/nanda/.gemini/antigravity/brain/90cf4292-95ed-4135-9490-f0b353c8cdff/gas_portal_mockup_1775145182491.png)

---

## 📂 Project Structure

```text
├── src/main/java/com/gasbooking
│   ├── controller/      # API/Web Controllers
│   ├── model/           # Entity Classes (Orders, Stocks, User, Agency)
│   ├── repository/      # JPA Repositories
│   └── GasBookingApp    # Main Application Class
├── src/main/resources
│   ├── templates/       # Thymeleaf HTML Templates
│   └── static/          # CSS, Images, JS
└── pom.xml             # Maven Project Configuration
```

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.

---

<p align="center">
  Developed by <b>nandanaak2306-sys</b>
</p>
