                **MusicPlayer(Console) User Manual**
                
-------------------
**Introduction**
-------------------
The *MusicPlayer(Console)* application is a Java-based music player that runs in a console/terminal window. 
To make it easy for users to run the application across different platforms (Windows, Linux, macOS), 
this package includes batch scripts for Windows and shell scripts for Linux/macOS.

**Files in the package:**
    --> MusicPlayer(Console).jar   - The main JAR file containing the application.
    --> MusicPlayer(Console).bat   - Batch script to run the application on Windows.
    --> MusicPlayer(Console).sh    - Shell script to run the application on Linux and macOS.
    
---------------------------------
**For Windows Users:**
---------------------------------

**Prerequisites:**
    --> **Java**: Ensure Java is installed by running the following command in Command Prompt:
    ```
    java --version
    ```
  
**How to Run the Application:**

--> **Option 1: Run using the Batch Script:**
    1. Double-click on the `MusicPlayer(Console).bat` file to run the application.
  
--> **Option 2: Run via Command Prompt:**
    1. Open **Command Prompt**.
    2. Navigate to the folder containing `MusicPlayer(Console).bat`:
    3. Run the batch file:
    ```
    MusicPlayer(Console).bat
    ```

--> **Option 3: Run the JAR File Directly:**
    1. Open **Command Prompt**.
    2. Navigate to the folder containing `MusicPlayer(Console).jar`:
    3. Run the JAR file:
    ```
    java -jar MusicPlayer(Console).jar
    ```

-----------------------------------
**For Linux/macOS Users:**
-----------------------------------

**Prerequisites:**
    --> **Java**: Ensure Java is installed by running the following command in Terminal:
    ```
    java --version
    ```

**How to Run the Application:**

--> **Option 1: Run using the Shell Script:**
    1. Open **Terminal**.
    2. Navigate to the folder where the files are stored:
    3. Run the shell script:
    ```
    ./MusicPlayer(Console).sh
    ```
    4. If you get a permission denied error, make sure the script is executable:
    ```
    chmod +x MusicPlayer(Console).sh
    ```

--> **Option 2: Run the JAR File Directly:**
    1. Open **Terminal**.
    2. Navigate to the folder containing `MusicPlayer(Console).jar`:
    3. Run the following command:
    
    ```
    java -jar MusicPlayer(Console).jar
    ```

--------------------------------
This guide helps users easily run the **MusicPlayer(Console)** application on Windows, Linux, and macOS.
