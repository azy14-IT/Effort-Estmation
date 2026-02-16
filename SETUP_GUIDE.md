# Setup Guide for Employee Effort Tracking System

Since the necessary tools (`node`, `npm`, `mysql`) were not detected in the current environment, the system has been fully implemented in the code but requires a proper environment to run.

## Prerequisites
- **Node.js** (v16 or higher)
- **MySQL** (v8.0)
- **Docker** (Optional, for containerized run)

## Option 1: Running Locally (Manual)

1.  **Install Dependencies**:
    ```bash
    npm install
    ```

2.  **Setup Database**:
    - Ensure your MySQL server is running.
    - Create the database and tables using the provided script:
      ```bash
      # If you have 'mysql' in your path:
      mysql -u root < database/schema.sql
      
      # OR using the Node helper script (requires db connection):
      npm run init-db
      ```

3.  **Start the Server**:
    ```bash
    npm start
    ```
    The server will start at `http://localhost:3000`.

4.  **Access the App**:
    Open your browser to `http://localhost:3000`.

## Option 2: Running with Docker

1.  **Build and Run**:
    ```bash
    docker-compose up --build
    ```
    This will automatically set up the MySQL database and the Node.js application.

## Demo Credentials
(As per seed data in `database/schema.sql` and `config/db.js` logic)

- **Email**: `admin@company.com`
- **Password**: `admin123`

## Project Structure Overiew
- **Backend**: Express.js REST API (`server.js`, `controllers/`, `routes/`)
- **Frontend**: Vanilla JS/HTML/CSS SPA (`public/`)
- **Database**: MySQL schema (`database/schema.sql`)
