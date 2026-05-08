# TypeAudit

TypeAudit is a Java Swing application for practicing and tracking typing speed and accuracy. It provides real-time performance metrics during typing tests and maintains persistent statistics across sessions to help users monitor their progress over time.

## Features

### Core Typing Test Engine
- Real-time typing test with automatic completion detection (when last word matches)
- Case-insensitive character comparison for flexible typing standards
- Words Per Minute (WPM) calculation using standard formula: `(characters / 5) / time in minutes`

### Performance Metrics
- **Live Accuracy**: Updates dynamically as user types (based on characters typed)
- **Final Accuracy**: Calculated at test completion (based on expected text length)
- **Live WPM**: Real-time speed calculation updated every 500ms
- **Error Tracking**: Counts incorrect keystrokes per session

### Analytics & Trends
- Aggregate statistics: total sessions, best WPM, average WPM, average accuracy
- Performance trends: detecting improvement, decline, or stability in typing speed
- Recent sessions history: displays last 3 completed tests with timestamps
- Lifetime counters using `BigInteger` to handle high keystroke volumes

### Data Persistence
- Automatic serialization of all statistics to `stats.dat`
- Session-level data preserved: typed text, expected text, accuracy, WPM, duration, errors
- Timestamped activity logging to `log.txt` for debugging and auditing
- Graceful data recovery with fallback to fresh stats on corruption

### User Interface
- Single-screen Swing GUI with responsive layout
- Real-time metric display panel showing current performance
- Statistics panel with analytics and recent session history
- Native system look-and-feel for familiar OS appearance

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher

### Compilation

From the project root directory:
```powershell
javac T12/MainApp.java T12/core/TypingEngine.java T12/stats/*.java T12/ui/*.java T12/ui/panels/*.java T12/util/*.java
```

Or use your IDE to compile the entire T12 package.

### Running

```powershell
java T12.MainApp
```

The application will launch with the TypeAudit window. If `stats.dat` exists from a previous session, your historical statistics will be loaded automatically.

## Usage

1. **Start a Test**: Click the "Start" button to begin a typing session
2. **Type**: A random passage will appear in the top text area; type it exactly in the bottom area
3. **Auto-Complete**: The test ends automatically when you complete the last word correctly
4. **View Results**: Your WPM, accuracy, and error count display immediately upon completion
5. **Track Progress**: Statistics panel updates automatically showing your latest results and trends

## Project Structure

```
T12/
├── MainApp.java                 # Application entry point
├── core/
│   └── TypingEngine.java       # Core typing mechanics and metric calculations
├── stats/
│   ├── StatsRecord.java        # Data model for a single test session
│   ├── StatsManager.java       # Repository for all statistics with aggregation
│   └── AnalyticsEngine.java    # Trend and analytics computation
├── ui/
│   ├── MainFrame.java          # Main application window container
│   └── panels/
│       ├── TypingPanel.java    # Typing test UI and test lifecycle management
│       └── StatsPanel.java     # Statistics display and refresh
└── util/
    ├── FileManager.java        # Serialization and activity logging
    └── TextProvider.java       # Random passage selection for tests
```

## Architecture

### Data Flow
1. **User types** → TypingPanel detects input changes
2. **TypingPanel** calls TypingEngine to calculate metrics
3. **Test completes** → StatsRecord created and added to StatsManager
4. **FileManager** serializes updated StatsManager to disk
5. **Callback triggered** → StatsPanel refreshes display with new analytics

### Component Responsibilities

- **TypingEngine**: Pure calculation logic - WPM, accuracy, character comparison
- **StatsManager**: Maintains history and aggregates lifetime statistics
- **AnalyticsEngine**: Computes averages and trends from historical data
- **TypingPanel**: Manages test UI and state transitions
- **StatsPanel**: Displays current analytics and recent sessions
- **FileManager**: Handles all I/O operations (serialization, logging)

## Data Files

- `stats.dat`: Serialized StatsManager object containing complete session history and aggregated statistics
- `log.txt`: Timestamped activity log for application behavior tracking and error diagnosis

## Code Quality

All Java classes include:
- **Class-level documentation**: Purpose, key functionality, and component integration
- **Inline comments**: Context for why decisions are made, edge case handling, and technical details
- **Method documentation**: Input/output expectations and behavioral notes

## Technical Details

### Statistics Aggregation
- Uses `BigInteger` for lifetime counters to prevent overflow on high keystroke counts
- Tracks: total keystrokes, total words (estimated), total sessions, total characters typed
- Calculates best session by WPM; recent sessions by chronological order

### Accuracy Calculations
- **Live Accuracy**: `(correct_chars / typed_length) * 100` - updates during typing
- **Final Accuracy**: `(correct_chars / expected_length) * 100` - at test completion
- Both use case-insensitive character comparison

### Trend Detection
- Analyzes last 5 sessions to determine speed trend
- Returns: "Improving (+X WPM)", "Declining (-X WPM)", or "Stable"
- Requires minimum 2 sessions for meaningful trends

## Future Enhancements

Potential improvements for future versions:
- Multiple difficulty levels or passage categories
- Leaderboard or ranking system
- Export statistics to CSV/PDF
- Customizable test duration or passage length
- Visual charts for performance tracking
- Network multiplayer typing races
