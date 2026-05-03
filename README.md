# TypeAudit - Typing Analytics Tool

TypeAudit is a sophisticated typing performance analyzer built with Java Swing. It tracks typing speed, accuracy, and detailed character-level metrics to help users improve their typing efficiency.

## Features

- **Real-time Analytics**: Tracks WPM (Words Per Minute) and accuracy as you type.
- **Character-level Tracking**: Identifies which keys are causing the most errors.
- **Persistent Stats**: Saves your progress across sessions using object serialization.
- **Clean UI**: A minimalist and intuitive Swing-based interface.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher.

### Compilation

To compile the project, navigate to the root directory and run:

```powershell
javac T12/MainApp.java
```

*Note: This will automatically compile all dependency classes in the `T12` package.*

### Running the Application

After compilation, run the application using:

```powershell
java T12.MainApp
```

## Project Structure

- `T12.MainApp`: Entry point of the application.
- `T12.core`: Core typing logic and detection.
- `T12.ui`: User interface components and panels.
- `T12.stats`: Analytics engine and statistics management.
- `T12.util`: File management and utility classes.

---
Created with ❤️ by Antigravity
