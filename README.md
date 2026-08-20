# Historical Value at Risk (VaR) Calculator
---
## 1. Project Overview

This project is a **self-contained Java implementation of a Historical Value at Risk (VaR) calculator**, developed to demonstrate clear, testable financial-data processing and risk-calculation logic.

The application calculates VaR for:
- a single trade, and
- an aggregated portfolio of trades,

using historical daily PnL data read from CSV files.

The implementation focuses on:
- clear separation of concerns,
- explicit assumptions and validation,
- deterministic and reviewable behaviour,
- and code clarity over production-scale optimisation.

This is a simplified portfolio project focused on correctness, validation and reviewable design.
It does not include any proprietary, confidential, or production data.
---
## 2. High-level design
The application is structured into clear layers:
### **app/Main**
Entry point of the application.
Responsible for orchestration only: selecting input data, confidence level, and portfolio weights, and invoking the loader and calculation components.
### **io/CSVDataLoader**
Instance-based loader that reads trade PnL series from CSV files into an in-memory map (tradesPool).  
Provides daily PnL for a single trade via `getTradeDailyPnL(tradeId)`.
### **domain/PortfolioPnLAggregator**
Stateless domain component responsible for:
- validating portfolio weights
- ensuring equal PnL series length across trades
- aggregating weighted daily PnL across trades
### **service/VaRCalculator**
Core calculation component.
Computes the VaR from the given PnL and confidence level.
Validates input parameters.
### **data/**
CSV files containing historical daily PnL data.

This design ensures the core calculation component remains testable, reusable and encapsulated.
---
## 3. Assumptions
- Historical VaR methodology using a nearest-rank (ceiling) percentile approach, with no interpolation. 
  e.g. With 260 daily observations and a 99% confidence level, the VaR corresponds to the 3rd worst daily PnL.
  Formally, the index is determined as: `ceil((1 - confidence) × N)`.
- Confidence level must be within (0, 1).
- Daily PnL values are aligned by index across trades.
- Portfolio weights sum to 1.0 (tolerance ±1e-6).
- Each CSV only contains one single column of daily PnL with a header row.
- VaR outputs a negative PnL threshold; e.g. -83.43 means a loss of 83.43 in the selected percentile.
- Input data is mock historical data. 
  Random numbers are generated using Google Sheets with mean 0 and different variances (50, 100, 150) to mimic realistic scenarios.
- This implementation prioritises correctness and reviewability over performance, and does not include optimisations for large datasets or production-scale execution.
---
## 4. How to run

This project requires JDK 17 or later.
From the project root directory:

```bash
rm -rf build
mkdir -p build
javac -d build $(find src/main/java -name "*.java")
java -cp build var.app.Main
```
---
## 5. Testing
Prepared unit tests covering VaR calculation logic, including:
- happy path calculation
- confidence boundary validation
- invalid PnL inputs