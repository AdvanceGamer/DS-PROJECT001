
                BrowserHistoryManager(Console) User Manual
                
-------------------
**Introduction**
-------------------
The *BrowserHistoryManager(Console)* application is a Java-based tool designed for managing, viewing, and analyzing browser history directly from a console or terminal. 
This package includes batch scripts for Windows and shell scripts for Linux/macOS, making it easy to run the application across different platforms.

**Files in the package:**
   - **BrowserHistoryManager(Console).jar** - The main JAR file containing the application.
   - **BrowserHistoryManager(Console).bat** - Batch script to run the application on Windows.
   - **BrowserHistoryManager(Console).sh** - Shell script to run the application on Linux and macOS.
    
---------------------------------
**For Windows Users:**
---------------------------------

**Prerequisites:**
   - **Java**: Ensure Java is installed by running the following command in Command Prompt:
   ```
   java --version
   ```

**How to Run the Application:**

- **Option 1: Run using the Batch Script:**
   1. Double-click on the `BrowserHistoryManager(Console).bat` file to run the application.
  
- **Option 2: Run via Command Prompt:**
   1. Open **Command Prompt**.
   2. Navigate to the folder containing `BrowserHistoryManager(Console).bat`.
   3. Run the batch file:
   ```
   BrowserHistoryManager(Console).bat
   ```

- **Option 3: Run the JAR File Directly:**
   1. Open **Command Prompt**.
   2. Navigate to the folder containing `BrowserHistoryManager(Console).jar`.
   3. Run the JAR file:
   ```
   java -jar BrowserHistoryManager(Console).jar
   ```

-----------------------------------
**For Linux/macOS Users:**
-----------------------------------

**Prerequisites:**
   - **Java**: Ensure Java is installed by running the following command in Terminal:
   ```
   java --version
   ```

**How to Run the Application:**

- **Option 1: Run using the Shell Script:**
   1. Open **Terminal**.
   2. Navigate to the folder where the files are stored.
   3. Run the shell script:
   ```
   ./BrowserHistoryManager(Console).sh
   ```
   4. If you encounter a "permission denied" error, ensure the script is executable:
   ```
   chmod +x BrowserHistoryManager(Console).sh
   ```

- **Option 2: Run the JAR File Directly:**
   1. Open **Terminal**.
   2. Navigate to the folder containing `BrowserHistoryManager(Console).jar`.
   3. Run the following command:
   ```
   java -jar BrowserHistoryManager(Console).jar
   ```

--------------------------------
This guide enables users to easily run the **BrowserHistoryManager(Console)** application on Windows, Linux, and macOS.
