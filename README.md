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

### Dev/Quality Tools

- SonarQube (via Maven scanner + `sonar-project.properties`)
- Git

---

## 3) High-Level Architecture

- **Frontend (`frontend`)**
  - Shows login/register screens
  - Uses JWT token for protected API calls
  - Dashboard handles contacts CRUD + search + pagination + import/export
  - Profile page handles password change and logout
  - Contact details page shows complete contact profile

- **Backend (`backend`)**
  - REST APIs under `/api/auth` and `/api/contacts`
  - Auth flow returns JWT token on login
  - JWT filter validates token on protected requests
  - Service layer contains business logic
  - Repository layer handles DB queries through JPA
  - Global exception handler provides clean error messages

---

## 4) Important API Endpoints

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/change-password`

### Contacts

- `GET /api/contacts` (paginated + searchable)
- `GET /api/contacts/{id}`
- `POST /api/contacts`
- `PUT /api/contacts/{id}`
- `DELETE /api/contacts/{id}`
- `GET /api/contacts/export`
- `POST /api/contacts/import`

---

## 5) How Authentication Works

1. User logs in with email/phone + password.
2. Backend validates credentials and returns JWT.
3. Frontend stores token in `localStorage`.
4. Axios interceptor automatically sends `Authorization: Bearer <token>`.
5. If token is invalid/expired, frontend clears session and redirects to `/login`.

---

## 6) Database Setup

### Default: SQL Server

Backend `application.properties` uses SQL Server by default:

- `SQLSERVER_URL`
- `SQLSERVER_USERNAME`
- `SQLSERVER_PASSWORD`

Schema reference is available in `schema.sql`.

### Optional local fallback: H2 profile

You can run backend with H2 profile using:

`-Dspring-boot.run.profiles=h2`

---

## 7) Environment Variables

Use `.env.example` as reference:

- `JWT_SECRET`
- `SQLSERVER_URL`
- `SQLSERVER_USERNAME`
- `SQLSERVER_PASSWORD`
- `DB_PASSWORD` (used by H2 profile)

---

## 8) How to Run (Step by Step)

Open **two terminals**.

### Terminal 1: Backend (SQL Server default)

```bat
cd /d "C:\Users\aima khan\OneDrive\Documents\contact managment system\backend"
set SQLSERVER_URL=jdbc:sqlserver://localhost:1433;databaseName=ContactMgmtDB;encrypt=true;trustServerCertificate=true
set SQLSERVER_USERNAME=sa
set SQLSERVER_PASSWORD=YourStrong@Passw0rd
set JWT_SECRET=replace_with_secure_secret
apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
```

### Optional Backend with H2 fallback

```bat
cd /d "C:\Users\aima khan\OneDrive\Documents\contact managment system\backend"
set JWT_SECRET=replace_with_secure_secret
apache-maven-3.9.6\bin\mvn.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

### Terminal 2: Frontend

```bat
cd /d "C:\Users\aima khan\OneDrive\Documents\contact managment system\frontend"
npm install
npm run dev
```

### Open in browser

- Frontend: <http://localhost:5173>
- Backend base: <http://localhost:8080/api>

---

## 9) Testing

Backend tests include:

- Service layer tests
- Controller layer test
- Repository/data-access layer test

Run tests:

```bat
cd /d "C:\Users\aima khan\OneDrive\Documents\contact managment system\backend"
apache-maven-3.9.6\bin\mvn.cmd test
```

---

## 10) SonarQube

Config files:

- `sonar-project.properties`
- Sonar Maven plugin in `backend/pom.xml`

Run analysis:

```bat
cd /d "C:\Users\aima khan\OneDrive\Documents\contact managment system"
backend\apache-maven-3.9.6\bin\mvn.cmd -f backend\pom.xml sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=YOUR_TOKEN
```

---

## 11) Logging and Error Handling

- Application logs are written with SLF4J/Logback.
- Important actions and failures are logged in backend services.
- Global exception handler converts runtime/validation errors into meaningful API responses.

---

## 12) Project Structure

```text
contact managment system/
  backend/                      # Spring Boot backend
    src/main/java/...           # controllers, services, entities, security, repositories
    src/main/resources/         # application configs
    src/test/java/...           # unit/integration tests
  frontend/                     # React app
    src/pages/                  # dashboard, login, register, profile, contact details
    src/services/               # axios services
    src/context/                # auth context
  schema.sql                    # SQL schema reference
  sonar-project.properties      # SonarQube configuration
  .env.example                  # Environment variable template
```

---

## 13) Notes for GitHub

- Keep secrets out of git (use env vars).
- `.gitignore` already excludes logs, DB runtime files, `target`, `node_modules`, etc.
- Recommended workflow: feature branches + pull requests before merging to main.
