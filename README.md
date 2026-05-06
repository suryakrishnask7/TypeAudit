# TypeAudit

TypeAudit is a small Java Swing typing app. It keeps the core typing engine, live analytics, persistent stats, and a simple single-screen UI.

## Features

- Core typing test engine with WPM, accuracy, and completion logic.
- Live WPM, accuracy, and time.
- Persistent analytics using serialized stats.
- `BigInteger` lifetime counters in `StatsManager`.
- Simplified Swing UI.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher.

### Compilation

```powershell
javac T12/MainApp.java
```

### Running

```powershell
java T12.MainApp
```

## Project Structure

- `T12.MainApp`: Entry point.
- `T12.core`: The typing engine.
- `T12.stats`: Analytics and persistent stats.
- `T12.ui`: Simplified Swing interface.
- `T12.util`: Text and stats file helpers.
