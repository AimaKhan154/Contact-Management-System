# Contact Management System

A full-stack web application to manage personal/professional contacts with authentication, search, pagination, profile management, import/export, logging, testing, and code-quality tooling.

---

## 1) Project Overview

This system allows users to:

- Register using **email or phone**
- Log in and manage their own session
- Change password from profile
- Create, update, delete, search, and view contacts
- Import contacts from CSV and export contacts to CSV

Each contact stores:

- Title
- First Name
- Last Name
- Email(s) with labels
- Phone number(s) with labels

---

## 2) Technology Stack

### Backend

- Java 21+
- Spring Boot
- Spring Security (JWT auth)
- Spring Data JPA + Hibernate
- SQL Server (default runtime database)
- H2 (optional local fallback profile)
- SLF4J + Logback
- JUnit 5 + Mockito
- Maven

### Frontend

- React.js
- Vite
- Axios
- React Router
- Lucide React Icons

