# 🎿 Ski Resort Tycoon

A Java-based ski resort management game inspired by RollerCoaster Tycoon, built with clean architecture and scalability in mind.

## 🌟 Project Overview

Ski Resort Tycoon is a management simulation game where players build and operate a ski resort. The project focuses on:

- **Clean Architecture**: Separation of concerns with core business logic isolated from UI and infrastructure
- **Component-Based Design**: Each major feature (Mountain, Guests, Finances) is a self-contained component
- **Advanced Graphics**: Modern JavaFX-based isometric rendering system with camera controls
- **Scalability**: Modular structure allows easy addition of new features without breaking existing functionality
- **Test-Driven Development**: Comprehensive test coverage (65+ tests) ensures reliability as the codebase grows

## 🏗️ Architecture

### Component Structure
Each major game feature follows a consistent structure:
```
components/
├── mountain/
│   ├── core/          # Business logic and entities
│   ├── visual/        # ASCII rendering for debugging
│   └── tests/         # Component-specific tests
├── guests/            # Guest management system ✅
├── finances/          # Financial system ✅  
└── graphics/          # JavaFX isometric rendering ✅
```

### Clean Architecture Layers
- **Core**: Domain entities and business rules
- **Components**: Feature-specific modules with full visual and test coverage
- **Graphics**: Modern JavaFX-based isometric rendering system
- **Shared**: Common utilities and value objects
- **App**: Multiple application entry points (Console, JavaFX GUI, Demos)

## 🚀 Current Features

### Mountain Component ✅
- **Dynamic Terrain Generation**: Procedural mountain elevation with realistic slopes
- **Ski Slope Management**: Build slopes with different difficulty levels (Beginner, Intermediate, Advanced, Expert)
- **Ski Lift System**: Multiple lift types (Chairlift, Gondola, T-Bar, Magic Carpet) with capacity management
- **Validation System**: Ensures slopes go downhill and lifts go uphill based on terrain elevation
- **Visual Rendering**: Both ASCII and JavaFX isometric visualization
- **Appeal System**: Dynamic scoring for attractions based on conditions and crowding

### Guest Component ✅
- **Individual Guest Entities**: Each guest has skill level, satisfaction, and decision-making AI
- **Realistic Behavior**: Guests choose slopes based on skill level and satisfaction
- **Lifecycle Management**: Spawning, activity selection, and departure based on satisfaction
- **Capacity Management**: Resort capacity limits and guest flow control
- **Statistics Tracking**: Comprehensive guest analytics and reporting
- **Visual Integration**: Guest representation in both ASCII and isometric views

### Finance Component ✅
- **Challenging Economic Progression**: Start with limited funds, require strategic planning
- **Comprehensive Revenue Tracking**: 17 different revenue sources from operations
- **Detailed Expense Management**: 26 expense categories covering all resort operations
- **Advanced Loan System**: Credit ratings, dynamic interest rates, bankruptcy protection
- **Dynamic Pricing**: Guest satisfaction affects willingness to pay for tickets
- **Financial Progression**: Multiple difficulty tiers from survival to empire building
- **Realistic Operating Costs**: Daily expenses, utilities, maintenance, and staff costs

### Graphics Component ✅ NEW!
- **Modern JavaFX Rendering**: Hardware-accelerated 2D graphics with isometric perspective
- **Advanced Camera System**: Pan, zoom, and explore the resort with smooth controls
- **Isometric Mathematics**: Proper coordinate conversion for classic tycoon game feel
- **Terrain Visualization**: Elevation-based coloring and tile rendering
- **Infrastructure Rendering**: Visual representation of slopes, lifts, and guest movement
- **Performance Optimized**: Frustum culling and efficient tile rendering
- **Comprehensive Testing**: 21 graphics tests covering math, camera, and rendering

### Technical Features
- **Maven Build System**: Professional project structure with dependency management
- **Comprehensive Testing**: 65+ test cases covering all components (JUnit 5)
- **Java 17**: Modern Java features and best practices
- **JavaFX Graphics**: Modern 2D graphics with isometric rendering
- **Logging**: SLF4J with Logback for proper logging infrastructure
- **JSON Support**: Jackson for future save/load functionality

## 🎮 Getting Started

### Prerequisites
- Java 17 or later
- Maven 3.6 or later
- JavaFX 17+ (included as dependency)

### Installation
1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```bash
   mvn clean compile
   ```

### Running the Applications

#### JavaFX Isometric GUI (Recommended)
```bash
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonGUI"
```
Features: Interactive isometric view, mouse controls, real-time rendering

#### Console Demos
```bash
# Mountain and infrastructure demo
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonDemo"

# Guest system simulation
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonGuestDemo"

# Financial system progression
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonFinanceDemo"

# Visual components verification
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonSimpleDemo"
```

### Running Tests
```bash
mvn test
```

**Test Coverage**: 65 comprehensive test cases
- Mountain Component: 11 tests
- Guest Component: 13 tests  
- Finance Component: 20 tests
- Graphics Component: 21 tests

## 📊 JavaFX Isometric View

The new JavaFX application features:

- **Isometric Perspective**: Classic RollerCoaster Tycoon-style fixed camera angle
- **Interactive Controls**: 
  - Mouse drag to pan around the resort
  - Mouse wheel to zoom in/out
  - Click to show world coordinates (debug)
- **Real-time Rendering**: Smooth 60fps graphics with proper depth sorting
- **Visual Elements**:
  - Terrain tiles with elevation-based coloring
  - Slopes rendered with difficulty-appropriate colors (Green/Blue/Black/Red)
  - Lifts with stations and connecting lines
  - UI overlay with camera info and controls

### Controls
- **Pan**: Click and drag to move around
- **Zoom**: Mouse wheel to zoom in/out
- **Debug**: Click anywhere to see world coordinates

## 🧪 Testing Philosophy

The project follows a test-first approach with comprehensive coverage:

### Component Testing
- **Mountain**: Terrain generation, slope/lift validation, elevation calculations
- **Guest**: Behavior simulation, satisfaction tracking, lifecycle management
- **Finance**: Economic calculations, loan system, revenue/expense tracking
- **Graphics**: Coordinate math, camera controls, rendering logic

### Integration Testing
- Cross-component interactions (guests using slopes/lifts, financial impacts)
- End-to-end scenarios through demo applications
- Visual verification through ASCII and JavaFX rendering

### Performance Testing
- Large mountain handling (100x100 grids)
- High guest capacity scenarios (100+ concurrent guests)
- Rendering performance with camera edge cases

## 🔧 Core Classes

### Mountain Component
- **`Mountain`**: Core terrain and infrastructure management
- **`Slope`**: Ski slope entities with difficulty levels and appeal scoring
- **`Lift`**: Ski lift entities with capacity and throughput management
- **`Position`**: Immutable 2D coordinate system

### Guest Component
- **`Guest`**: Individual guest entities with AI and satisfaction
- **`GuestManager`**: Lifecycle management and spawning control
- **`GuestActivity`**: Decision-making system for guest behavior
- **`GuestRenderer`**: ASCII visualization and statistics

### Finance Component
- **`FinanceManager`**: Core financial operations and tracking
- **`ExpenseCategory`**: Categorized expense management
- **`RevenueSource`**: Revenue tracking and calculation
- **`FinanceRenderer`**: Financial dashboards and reporting

### Graphics Component
- **`IsometricMath`**: Coordinate conversion and tile mathematics
- **`Camera2D`**: Pan/zoom camera system with smooth movement
- **`TileRenderer`**: Efficient terrain rendering with frustum culling
- **`SkiResortTycoonGUI`**: Main JavaFX application

### Shared Utilities
- **`GameConstants`**: Centralized configuration values
- **`Position`**: Coordinate system with distance calculations

## 🚧 Known Issues

### JavaFX on macOS
There's a known issue with JavaFX on macOS related to NSTrackingArea management that can cause crashes during window resizing. This is a platform-specific JavaFX bug, not an issue with our code. Workarounds:

1. Run on Linux or Windows for stable JavaFX experience
2. Use the console demos which work perfectly on all platforms
3. Avoid rapid window resizing when running on macOS

The application initializes correctly and renders properly before the crash occurs.

## 🎯 Planned Features

### Enhanced Graphics
- **3D Elements**: Pseudo-3D trees, buildings, and decorations
- **Weather Effects**: Snow particles, fog, and dynamic weather visualization
- **Guest Animation**: Animated skiers moving down slopes and riding lifts
- **UI Menus**: In-game construction and management interfaces

### Advanced Gameplay
- **Weather & Seasons**: Snow quality, weather events, seasonal guest flow
- **Staff Management**: Lift operators, ski patrol, maintenance crews
- **Infrastructure**: Lodges, restaurants, rental shops, parking
- **Scenarios**: Challenge modes and objectives

### Save/Load System
- **Game Persistence**: Save resort progress and continue later
- **Scenario Editor**: Create custom mountains and challenges
- **Statistics Tracking**: Long-term performance analytics

## 📈 Performance Considerations

- **Efficient Rendering**: Frustum culling ensures only visible tiles are drawn
- **Memory Optimization**: Immutable value objects reduce garbage collection
- **Scalable Architecture**: Components handle large resorts (100x100+ mountains)
- **Optimized Calculations**: Cached elevation lookups and distance calculations

## 🤝 Contributing

The project architecture supports easy extension:

1. **Add New Components**: Follow the established core/visual/test pattern
2. **Extend Graphics**: Enhance the isometric rendering system
3. **Add Game Features**: Build on the solid financial and guest foundations
4. **Improve Testing**: Maintain the comprehensive test coverage

## 📝 Version History

### v1.0.0-SNAPSHOT (Current)
- ✅ Complete Mountain component with terrain generation and validation
- ✅ Advanced Guest system with AI and satisfaction tracking
- ✅ Comprehensive Financial system with loans and dynamic pricing
- ✅ Modern JavaFX graphics with isometric rendering
- ✅ Professional camera system with smooth controls
- ✅ Comprehensive test suite (65 test cases)
- ✅ Multiple demo applications showcasing features
- ✅ ASCII rendering for debugging and console use

### Upcoming v1.1.0
- 🚧 Enhanced UI menus and construction interfaces
- 🚧 Weather system integration
- 🚧 Save/load functionality
- 🚧 Guest animation and movement visualization

## 🎯 Project Goals

1. **Learning Platform**: Demonstrate clean architecture and design patterns in Java
2. **Scalable Foundation**: Build a system that can grow into a full tycoon game  
3. **Modern Graphics**: Showcase JavaFX capabilities for 2D game development
4. **Test Excellence**: Maintain comprehensive test coverage as complexity grows
5. **Clean Code**: Prioritize readability and maintainability over features 