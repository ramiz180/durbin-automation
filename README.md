# Durbin Automation — Spring Boot + Selenium + TestNG Framework

A production-grade, API-driven test automation framework combining the power of **Spring Boot**, **Selenium WebDriver**, and **TestNG**. It exposes REST APIs that enable external systems (such as Postman, CRMs, or CI pipelines) to trigger browser automation tests remotely on `https://assets.durbin.co.in/` and retrieve real-time logs, timing, and error diagnostics.

---

## 🚀 Key Features

*   **API-Driven Test Triggering** — Start tests (Login, Asset Creation, Health Check) dynamically via HTTP `POST`/`GET` requests.
*   **Dynamic Data Injection** — Pass test parameters (credentials, asset codes, device numbers) straight from the REST payload.
*   **Chrome Native Execution** — Simplified browser driver management targeting Google Chrome (supporting both headless and headed modes).
*   **Thread-Safe Isolated Logging** — Every execution generates a separate, timestamped log file (`logs/<date>/<user>_<HH-mm-ss>.log`) along with real-time in-memory logs returned directly in the REST API response.
*   **Clean Design System (POM)** — Fully adheres to the Page Object Model (POM) separating base controllers, pages, utils, and tests.

---

## ⚙️ Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language** | Java 17 |
| **Browser Automation** | Selenium WebDriver 4.45.0 |
| **Test Runner** | TestNG 7.8.0 |
| **API Server** | Spring Boot 2.7.18 |
| **Build Tool** | Apache Maven 3.x |
| **Logging Engine** | Log4j2 2.24.2 |

---

## 📁 Project Directory Structure

```text
Durbin-Automation/
├── src/
│   ├── main/
│   │   ├── java/com/durbin/
│   │   │   ├── automation/
│   │   │   │   ├── AutomationApplication.java      # Spring Boot App Entry Point
│   │   │   │   │
│   │   │   │   ├── api/                            # API Orchestration Layer
│   │   │   │   │   ├── AutomationController.java   # HTTP REST Endpoints
│   │   │   │   │   ├── AutomationService.java      # TestNG Suite Execution Runner
│   │   │   │   │   ├── ApiResponse.java            # Standard JSON Response Model
│   │   │   │   │   ├── ErrorDetails.java           # Structured Failures Model
│   │   │   │   │   ├── ExecutionResultHolder.java  # Thread-Local Failure Catchers
│   │   │   │   │   └── TestDataHolder.java         # Thread-Local Test Data Store
│   │   │   │   │
│   │   │   │   ├── base/                           # Base Page Classes & Listeners
│   │   │   │   │   └── BasePage.java               # PageFactory base parent class
│   │   │   │   │
│   │   │   │   ├── pages/                          # Page Object Models (POM)
│   │   │   │   │   ├── LoginPage.java              # Login page locators & methods
│   │   │   │   │   └── AssetPage.java              # Create Asset page locators & methods
│   │   │   │   │
│   │   │   │   └── utils/                          # Helper Utilities
│   │   │   │       ├── ConfigReader.java           # Reads config.properties
│   │   │   │       ├── EnvReader.java              # Reads environment variables
│   │   │   │       ├── ExecutionLogCollector.java  # Captures real-time console log strings
│   │   │   │       ├── ExecutionLogManager.java    # Handles file-based run logs
│   │   │   │       └── LogUtils.java               # Log manipulation tools
│   │   │   │
│   │   │   └── tests/                              # TestNG Test Cases
│   │   │       ├── CreateAssetTest.java            # Dynamic Asset Creation scenario
│   │   │       ├── HealthCheckTest.java            # Basic site reachability verification
│   │   │       └── LoginTest.java                  # Login-only validation test
│   │   │
│   │   └── resources/
│   │       ├── config.properties                   # Main configurations (URLs)
│   │       └── log4j2-spring.xml                   # Log4j2 configuration
│   │
│   └── test/
│       └── java/com/durbin/
│           └── automation/
│               └── base/                           # TestNG Base Infrastructure
│                   ├── BaseTest.java               # WebDriver lifecycle setUp & tearDown
│                   ├── AnnotationTransformer.java  # Retry listener transformer
│                   ├── RetryAnalyzer.java          # Automation failures retry rules
│                   └── TestExecutionListener.java  # TestNG hooks capturing output
│
├── pom.xml                                         # Maven build configuration
└── .env                                            # Local environment variables
```

---

## 🛠️ Installation & Setup

1.  **Clone the workspace** and open a terminal inside the root directory `Durbin-Automation`.
2.  **Configure environment values**:
    Create a `.env` file in the root folder (using `.env.sample` as a template):
    ```env
    headless=false
    browsers=chrome
    ```
3.  **Configure Target Website URL**:
    Set target URLs in [config.properties](file:///c:/AI%20Project/Durbin-Automation/src/main/resources/config.properties):
    ```properties
    base.url=https://assets.durbin.co.in/
    login.url=https://assets.durbin.co.in/login
    explicit.wait=10
    ```

---

## ☕ Running the Server

Start the Spring Boot REST API server:

```powershell
mvn spring-boot:run
```
*The server will start listening on port `8080` (or `8081` if port 8080 is blocked).*

---

## 📡 API Reference & Endpoints

### 1. Login Verification Test
Runs a test verifying that the application can log in cleanly using the provided credentials.

*   **Endpoint**: `POST /automation/run-login`
*   **Request Body**:
    ```json
    {
      "login": {
        "username": "avijit@durbin.live",
        "password": "Maco@2023"
      }
    }
    ```
*   **Sample curl**:
    ```bash
    curl -X POST http://localhost:8080/automation/run-login \
      -H "Content-Type: application/json" \
      -d '{"login": {"username": "avijit@durbin.live", "password": "abc"}}'
    ```

### 2. Create Asset Test
Performs login and proceeds to automate basic asset creation details.

*   **Endpoint**: `POST /automation/run-create-asset`
*   **Request Body**:
    ```json
    {
      "login": {
        "username": "avijit@durbin.live",
        "password": "abc"
      },
      "asset": {
        "assetNumber": "ASSET-999",
        "deviceNumber": "DEV-101",
        "make": "TATA",
        "model": "Model-X",
        "serialNumber": "SN-999-ABC",
        "purchasedFrom": "Durbin Corp",
        "height": "12",
        "capacity": "150",
        "year": "2024",
        "location": "Warehouse A"
      }
    }
    ```
*   **Sample curl**:
    ```bash
    curl -X POST http://localhost:8080/automation/run-create-asset \
      -H "Content-Type: application/json" \
      -d '{
        "login": {
          "username": "avijit@durbin.live",
          "password": "Maco@2023"
        },
        "asset": {
          "assetNumber": "ASSET-999",
          "deviceNumber": "DEV-101",
          "make": "TATA",
          "model": "Model-X"
        }
      }'
    ```

### 3. Reachability Health Check
Checks if the target login url can be successfully reached by WebDriver.

*   **Endpoint**: `GET /automation/run-health-check`
*   **Sample curl**:
    ```bash
    curl -X GET http://localhost:8080/automation/run-health-check
    ```

---

## 📊 Structured API Responses

All test execution responses return a standard structured JSON:

```json
{
  "status": "PASSED",
  "message": "Create Asset test executed successfully",
  "executionTime": 14231,
  "logs": [
    "18:33:15 - Initializing WebDriver",
    "18:33:20 - Navigating to login page: https://assets.durbin.co.in/login",
    "18:33:22 - Logging in as: avijit@durbin.live",
    "18:33:28 - Clicked on Assets Menu",
    "18:33:30 - Clicked Save & Next Button"
  ],
  "error": null
}
```
If a test fails, the response status is returned as `FAILED` (with HTTP 500 status code) containing error details:
```json
{
  "status": "FAILED",
  "message": "Element not found: By.xpath: //button[.='Login']",
  "executionTime": 11500,
  "error": {
    "errorType": "ELEMENT_NOT_FOUND",
    "reason": "Element not found: By.xpath: //button[.='Login']",
    "suggestion": "Check XPath or CSS selector in AssetPage or LoginPage"
  }
}
```
