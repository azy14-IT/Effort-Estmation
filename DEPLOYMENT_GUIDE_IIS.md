# Deploying Spring Boot App on IIS (On-Premise Server)

This guide explains how to host your WorkTrack Pro application on a Windows Server using IIS as a reverse proxy, so it runs automatically without needing to manually start `run_app.bat`.

## Prerequisites

1.  **JDK 17**: Ensure Java 17 is installed on the server.
2.  **IIS (Internet Information Services)**: Enable this Windows Feature.
3.  **IIS Application Request Routing (ARR) 3.0**: Download and install from Microsoft.
4.  **IIS URL Rewrite Module**: Download and install from Microsoft.
5.  **WinSW (Windows Service Wrapper)**: To run the Spring Boot JAR as a Windows Service.

---

## Step 1: Build the Application
On your development machine (or server if Maven is installed):

1.  Open Command Prompt in the project folder.
2.  Run the build command:
    ```cmd
    mvn clean package -DskipTests
    ```
3.  This will create a `.jar` file in the `target/` folder (e.g., `employee-tracking-0.0.1-SNAPSHOT.jar`).

---

## Step 2: Run as a Windows Service (No more .bat files!)
To make the app run automatically when the server starts:

1.  **Create a Folder**: Create a folder on the server, e.g., `C:\WorkTrackPro`.
2.  **Copy Files**: Copy the generated `.jar` file from Step 1 to this folder.
3.  **Download WinSW**: Download `WinSW.NET4.exe` (or .NET Core version) from [WinSW GitHub](https://github.com/winsw/winsw/releases) and place it in the same folder.
4.  **Rename**: Rename `WinSW.NET4.exe` to `WorkTrackService.exe`.
5.  **Create Config**: Create a file named `WorkTrackService.xml` in the same folder with this content:

    ```xml
    <service>
      <id>WorkTrackPro</id>
      <name>WorkTrack Pro Service</name>
      <description>Employee Tracking Application System</description>
      <executable>java</executable>
      <arguments>-jar "C:\WorkTrackPro\employee-tracking-0.0.1-SNAPSHOT.jar"</arguments>
      <logmode>rotate</logmode>
    </service>
    ```
6.  **Install Service**: Open Command Prompt as Administrator, navigate to the folder, and run:
    ```cmd
    WorkTrackService.exe install
    WorkTrackService.exe start
    ```
    *Now your app runs in the background on port 8080 automatically!*

---

## Step 3: Configure IIS as a Reverse Proxy
To access the app via a proper web server (port 80) and Static IP:

1.  **Open IIS Manager**.
2.  **Enable Proxy**:
    *   Click on the Server node -> **Application Request Routing Cache**.
    *   Click **Server Proxy Settings** (right pane).
    *   Check **Enable proxy**. Click Apply.
3.  **Create Website**:
    *   Right-click **Sites** -> **Add Website**.
    *   **Site name**: `WorkTrackPro`.
    *   **Physical path**: Create an empty folder (e.g., `C:\inetpub\wwwroot\worktrack`) and select it.
    *   **Binding**: Select **http**, IP Address: **All Unassigned** (or select your Static IP), Port: **80**.
4.  **Setup URL Rewrite**:
    *   Click on your new Site (`WorkTrackPro`).
    *   Double-click **URL Rewrite**.
    *   Click **Add Rule(s)...** -> **Reverse Proxy**.
    *   **Inbound Rules**: Enter `localhost:8080`.
    *   Click OK.

---

## Step 4: Configure Static IP
To access the server from other machines using a fixed IP:

1.  Open **Network Connections** (`ncpa.cpl`).
2.  Right-click your network adapter -> **Properties**.
3.  Select **Internet Protocol Version 4 (TCP/IPv4)** -> **Properties**.
4.  Select **Use the following IP address**:
    *   **IP address**: Enter your static IP (e.g., `192.168.1.100` or public IP).
    *   **Subnet mask**: Usually `255.255.255.0`.
    *   **Default gateway**: Your router IP.
    *   **DNS**: Your preferred DNS (e.g., `8.8.8.8`).

---

## Accessing the App
Now, users can access the application by typing the Static IP address in their browser:
`http://192.168.1.100/` (or whatever IP you set).

**Benefits:**
*   No need to run `.bat` files manually.
*   App starts automatically if the server reboots.
*   Uses standard port 80 (no need to type :8080).
