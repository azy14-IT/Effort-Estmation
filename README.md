# Employee Effort & Working Hours Tracking System (Java Edition)

This system has been migrated to a **Java Spring Boot** application as per the requirement for "HTML CSS JAVA in front end" and "Backend only MySQL".

## Tech Stack
- **Backend:** Java (Spring Boot 3.1)
- **Frontend Layer:** Thymeleaf (Server-Side Java Templating) + HTML5 + CSS3
- **Database:** MySQL 8.0
- **Build Tool:** Maven

## Project Structure
```
/tracking
├── src/main/java/com/tracking/
│   ├── controller/      # Spring MVC Controllers (Page logic)
│   ├── model/           # JPA Entities (User, Project, Task)
│   ├── repository/      # Data Access Layer
│   └── TrackingApplication.java
├── src/main/resources/
│   ├── templates/       # HTML Views (Thymeleaf)
│   ├── static/          # CSS, Images
│   └── application.properties
├── database/schema.sql  # Database Initialization
└── pom.xml              # Project Dependencies
```

## How to Run

1.  **Prerequisites**: JDK 17+, Maven, MySQL.

2.  **Database Setup**:
    Create a database `employee_tracking` and run the schema script:
    ```bash
    mysql -u root -p employee_tracking < database/schema.sql
    ```

3.  **Run Application**:
    You can use the provided `run.bat` script, or manually:
    ```bash
    # Setting Maven Path (Example)
    set "PATH=C:\Program Files\apache-maven-3.9.12\bin;%PATH%"
    
    # Run
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080`.

4.  **Login**:
    - **Email**: `admin@company.com`
    - **Password**: `admin123` (Note: In this demo, password checking is simplified).

## "Java in Front End" Note
The application uses **Thymeleaf**, a modern Java template engine. This allows Java code to control the frontend rendering directly on the server, fulfilling the "Java Front End" architecture pattern (Server-Side Rendering).
