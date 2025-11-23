# JSON Tool

**JSON Tool** is a comprehensive full-stack web application designed to simplify the lifecycle of JSON Schema development. It provides a user-friendly interface for creating, editing, validating, and documenting JSON data structures.

> **Coursework Project** | Developed with Java Spring Boot & Vanilla JS.

---

## Features

### Core Functionality

* **Project Management**: Create and manage multiple projects to organize your schemas.
* **Dual-Pane Editor**: Real-time split view for JSON Schema and JSON Data.
* **Syntax Highlighting**: Integrated **Ace Editor** for a professional coding experience with error detection.

### Tools & Utilities

* **Validation**: Server-side validation of JSON Data against JSON Schema using standard specifications.
* **Visual Editor**: No-code table interface to add/edit schema properties, types, and descriptions.
* **Flat View**: Convert nested JSON structures into a flattened "dot-notation" list.
* **Markdown Export**: Auto-generate technical documentation tables from your schema.
* **Format & Minify**: One-click tools to beautify or compress your JSON code.

### Security & System

* **Secure Authentication**: User registration and login with BCrypt password hashing.
* **Version Control**: Automatic history tracking with the ability to restore previous file versions.
* **Admin Panel**: Dedicated dashboard for user management (Ban/Unban) and viewing system logs.

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Backend** | Java 21, Spring Boot 3.5, Spring Security, Spring Data JPA |
| **Database** | MySQL 8.0 |
| **Frontend** | HTML5, CSS3, Bootstrap 5, JavaScript (ES6+) |
| **Editor** | Ace Editor (via CDN) |
| **Build Tool** | Maven |

---

## Getting Started

Follow these instructions to set up the project locally.

### Prerequisites

* Java JDK 21+
* MySQL Server 8.0+
* Maven

### Installation

#### 1. Clone the repository

```bash
git clone https://github.com/Octopus663/jsontool.git
cd jsontool
```

#### 2. Database Setup

Create a database named `json_tool` in MySQL:

```sql
CREATE DATABASE json_tool;
```

#### 3. Configuration

Open `src/main/resources/application.properties` and update your MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/json_tool?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD
```

#### 4. Build and Run

**Using Maven Wrapper (recommended):**

##### Windows (PowerShell)

```powershell
.\mvnw clean install
.\mvnw spring-boot:run
```

##### Linux/macOS

```bash
./mvnw clean install
./mvnw spring-boot:run
```

---

### Troubleshooting: "No compiler is provided"

If you encounter the error **`No compiler is provided in this environment`**, follow these steps to manually configure your JDK path.

#### Step 1: Set JAVA_HOME

PowerShell needs to know where your JDK is located to run `javac`.

1. **Find your JDK Path:**
   - In IntelliJ IDEA, go to `File` → `Project Structure` → `SDKs`
   - Copy the **"Home Path"** for JDK 21+ (e.g., `C:\Users\User\.jdks\openjdk-21+...`)

2. **Set the environment variable:**
   ```powershell
   $env:JAVA_HOME = "C:\YOUR\PATH\TO\JDK-21+"
   ```

#### Step 2: Build and Launch

Once `JAVA_HOME` is set, use the Maven Wrapper to build and run:

**Build the project:**
```powershell
.\mvnw clean install
```

**Run the server:**
```powershell
.\mvnw spring-boot:run
```

---

#### 5. Access the App

Open your browser and navigate to:

```
http://localhost:8080
```

---

## 👨‍💼 Admin Setup

By default, new users are registered with the `USER` role. To access the Admin Panel:

1. Register a new user via the web interface (e.g., `admin@test.com`).
2. Access your database and run the following SQL command:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'admin@test.com';
   ```
3. Re-login. You will see a red **"Admin Panel"** button on the dashboard.

---

## 📁 Project Structure

```text
src/main/java/com/coursework/jsontool
├── config       # Security configurations
├── controller   # REST API endpoints
├── dto          # Data Transfer Objects
├── model        # JPA Entities (User, Project, FileVersion, etc.)
├── repository   # Database access interfaces
└── service      # Business logic (Facade, Singleton patterns)

src/main/resources/static
├── js/app.js    # Main frontend logic
├── index.html   # Login page
├── editor.html  # Main IDE interface
└── ...
```

---

## 📄 License

This project is a coursework assignment and is provided as-is for educational purposes.

---

## 📧 Contact

For questions or feedback, please reach out via GitHub issues.
Telegram: @Andrii_Kunda
