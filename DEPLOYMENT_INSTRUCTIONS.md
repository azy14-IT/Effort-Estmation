# 🚀 Free Deployment Guide for Employee Tracking App

Since your code is now on GitHub, follow these steps to deploy your application for free.

## Phase 1: Set Up a Free MySQL Database (Aiven)
You need a database before you deploy the app.

1.  **Sign Up**: Go to [Aiven.io](https://aiven.io/) and create a free account.
2.  **Create Service**:
    *   Click **Create service**.
    *   Select **MySQL**.
    *   Choose **Free Plan** (often labeled "Hobbyist" or similar).
    *   Select a cloud region (e.g., Google Cloud - Frankfurt).
    *   Give it a name (e.g., `tracking-db`).
    *   Click **Create Service**.
3.  **Get Credentials**:
    *   Once the service is "Running" (green dot), verify the `Connection Information`.
    *   Find the **Service URI**. It looks like:
        `mysql://avnadmin:password@host:port/defaultdb?ssl-mode=REQUIRED`
    *   **IMPORTANT**: You will need to change `mysql://` to `jdbc:mysql://` for your Spring Boot app.

---

## Phase 2: Deploy App to Render.com
Now deploy the Spring Boot application.

1.  **Sign Up**: Go to [Render.com](https://render.com/) and log in with your GitHub account.
2.  **New Web Service**:
    *   Click **New +** select **Web Service**.
    *   Connect your GitHub repository (`azy14-IT/Effort-Estmation`).
3.  **Configure**:
    *   **Name**: `employee-tracking`
    *   **Region**: Same as your database if possible (e.g., Frankfurt).
    *   **Runtime**: **Docker**.
    *   **Instance Type**: **Free**.
4.  **Environment Variables**:
    Scroll down to "Advanced" -> "Environment Variables" and add these three:

    | Key | Value |
    | :--- | :--- |
    | `DB_URL` | `jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?useSSL=true` (Copy from Aiven, change `mysql://` to `jdbc:mysql://`) |
    | `DB_USERNAME` | `avnadmin` (or whatever Aiven shows) |
    | `DB_PASSWORD` | `<YOUR_AIVEN_PASSWORD>` |

5.  **Deploy**:
    *   Click **Create Web Service**.
    *   Render will start building your app. This takes about 5-10 minutes.
    *   Watch the logs. Once it says "Started TrackingApplication", your app is live!

## Phase 3: Access Your App
Render will give you a URL like `https://employee-tracking.onrender.com`.
Click it to see your running application!
