# 📘 Spring Boot + Kafka Application

This is a personal project built using **Spring Boot** and **Apache Kafka**. It demonstrates how to develop a complete Kafka-based application, covering both **producer** and **consumer** sides along with proper testing, error handling, and persistence.

---

## 🔍 What This Project Does

### 🟢 Kafka Producer

- Exposes a **REST API** to accept library events from external clients.
- Uses `KafkaTemplate` to send messages to a Kafka topic.
- Demonstrates multiple ways to produce Kafka records:
  - With/without message keys
  - With custom headers
- Includes validations on incoming requests and a custom error handler.

### 🟡 Kafka Consumer

- Listens to the Kafka topic and consumes library events.
- Handles two event types: **NEW** and **UPDATE**.
- Processes messages and persists them using **Spring Data JPA** with **H2 in-memory database**.
- Demonstrates the use of **consumer groups** and **manual offset management**.
- Supports scalable consumption with configurable concurrency.

---

## 🧪 Testing

- **Unit tests** for controller and service layers using JUnit 5.
- **Integration tests** using **Embedded Kafka**:
  - For both producer and consumer logic
  - With actual Kafka topics and listeners
- **TestContainers** used for integration testing with real DB behavior.

---

## ⚠️ Error Handling & Retry

- Implements custom **error handlers** for both producer and consumer.
- Shows how to configure **retry logic**:
  - For specific exceptions
  - With fallback recovery logic
- Demonstrates handling of failures like broker unavailability and sync replica issues.

---

## 🧱 Technologies Used

- Java 17+
- Spring Boot
- Apache Kafka
- Spring Kafka
- Spring Data JPA
- H2 Database
- JUnit 5
- Embedded Kafka
- TestContainers

---


## ©️ License

© 2025 Lakshitha Chathuranga Srimal. All rights reserved.
