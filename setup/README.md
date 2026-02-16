# Professional Setup Guide for WorkTrack Pro

This folder contains scripts to automate the deployment process.

## 🚀 One-Click Installation

1.  **Build the Project**:
    Run this command in the project root:
    ```cmd
    mvn clean package -DskipTests
    ```
    *(Ensure you have Maven installed)*

2.  **Run the Installer**:
    *   Navigate to the `setup/` folder.
    *   Right-click `install.bat` -> **Run as Administrator**.
    *   Done! The app is now installed to `C:\WorkTrackPro` and running as a service.

---

## 🛠️ Included Files

*   **install.bat**: Automates folder creation, file copying, and service registration.
*   **uninstall.bat**: Stops and removes the service cleanly.
*   **WorkTrackPro-Service.xml**: Configuration for the Windows Service (WinSW).

## ⚠️ Important Requirement: WinSW

To make `install.bat` work *fully automatically* (service registration), you need the **WinSW** executable.

1.  Download `WinSW.NET4.exe` from: [WinSW Releases](https://github.com/winsw/winsw/releases)
2.  **Rename it** to `winsw.exe`.
3.  Place it inside this `setup/` folder.

Without this, `install.bat` will copy files but won't register the restart-on-boot service automatically.

---

## 🌐 IIS Configuration (After Installation)

Once installed, use IIS to make it accessible via port 80 (standard web):

1.  Open **IIS Manager** -> Create New Site -> Point to an empty folder.
2.  Set Binding to Port 80.
3.  Install **URL Rewrite Module**.
4.  Add a Reverse Proxy rule: `d` -> `localhost:8080`.
