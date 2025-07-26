# 🎿 Ski Resort Tycoon

A Java-based ski resort management game inspired by RollerCoaster Tycoon, built with clean architecture and scalability in mind.

## 🌟 Project Overview

Ski Resort Tycoon is a management simulation game where players build and operate a ski resort. The project focuses on:

- **Clean Architecture**: Separation of concerns with core business logic isolated from UI and infrastructure
- **Component-Based Design**: Each major feature (Mountain, Guests, Finances) is a self-contained component
- **Modern Graphics**: Professional Java Swing GUI with isometric 3D-style rendering
- **Scalability**: Modular structure allows easy addition of new features without breaking existing functionality
- **Test-Driven Development**: Comprehensive test coverage (59+ tests) ensures reliability as the codebase grows

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
└── graphics/          # Swing isometric rendering ✅
```

### Clean Architecture Layers
- **Core**: Domain entities and business rules
- **Components**: Feature-specific modules with full visual and test coverage
- **Graphics**: Professional Java Swing isometric rendering system
- **Shared**: Common utilities and value objects
- **App**: Multiple application entry points (Console demos, Swing GUI)

## 🚀 Current Features

### Mountain Component ✅
- **Dynamic Terrain Generation**: Procedural mountain elevation with realistic slopes
- **Ski Slope Management**: Build slopes with different difficulty levels (Beginner, Intermediate, Advanced, Expert)
- **Ski Lift System**: Multiple lift types (Chairlift, Gondola, T-Bar, Magic Carpet) with capacity management
- **Validation System**: Ensures slopes go downhill and lifts go uphill based on terrain elevation
- **Visual Rendering**: Both ASCII debug view and professional Swing isometric graphics
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

### Graphics Component ✅
- **Professional Swing GUI**: Cross-platform compatibility with native look and feel
- **Isometric 3D-Style Rendering**: Classic tycoon game perspective with proper depth
- **Advanced Camera System**: Pan, zoom, and explore the resort with smooth controls
- **Terrain Visualization**: Elevation-based coloring and diamond-shaped tile rendering
- **Infrastructure Rendering**: Visual representation of slopes, lifts, and guest movement
- **Performance Optimized**: Double-buffered rendering with viewport culling
- **Real-time Updates**: Live guest simulation and financial tracking in status bar

### Technical Features
- **Maven Build System**: Professional project structure with dependency management
- **Comprehensive Testing**: 59+ test cases covering all components (JUnit 5)
- **Java 17**: Modern Java features and best practices
- **Java Swing Graphics**: Rock-solid cross-platform GUI framework
- **Logging**: SLF4J with Logback for proper logging infrastructure
- **JSON Support**: Jackson for future save/load functionality

## 🎮 Getting Started

### Prerequisites
- Java 17 or later
- Maven 3.6 or later

### Installation
1. Clone the repository
2. Navigate to the project directory
3. Build the project:
   ```bash
   mvn clean compile
   ```

### Running the Game

#### Main Swing GUI (Recommended)
```bash
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonGUI"
```
**Features**: Professional isometric graphics, full mouse controls, real-time simulation

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

**Test Coverage**: 59 comprehensive test cases
- Mountain Component: 11 tests
- Guest Component: 13 tests  
- Finance Component: 20 tests
- Graphics Component: 15 tests

## 🎮 Swing GUI Features

The professional Swing GUI provides:

### Visual Features
- **Isometric 3D-Style View**: Classic RollerCoaster Tycoon perspective
- **Terrain Rendering**: Diamond-shaped tiles with elevation-based coloring
- **Infrastructure Display**: 
  - Slopes with difficulty color coding (🟢 Green, 🔵 Blue, ⚫ Black, 🔴 Red)
  - Lifts with stations and connecting lines
  - Guest visualization as animated yellow dots

### Interactive Controls
- **Mouse Drag**: Pan around the resort
- **Mouse Wheel**: Zoom in and out smoothly
- **Click Debug**: Click anywhere to see world coordinates
- **Status Bar**: Real-time guest count, money, and camera position

### Technical Excellence
- **Double-Buffered Rendering**: Smooth 30 FPS graphics
- **Viewport Culling**: Only renders visible terrain for performance
- **Cross-Platform**: Works perfectly on Windows, macOS, and Linux
- **System Integration**: Uses native look and feel

## 🧪 Testing Philosophy

The project follows a test-first approach with comprehensive coverage:

### Component Testing
- **Mountain**: Terrain generation, slope/lift validation, elevation calculations
- **Guest**: Behavior simulation, satisfaction tracking, lifecycle management
- **Finance**: Economic calculations, loan system, revenue/expense tracking
- **Graphics**: Coordinate math, camera controls, isometric rendering logic

### Integration Testing
- Cross-component interactions (guests using slopes/lifts, financial impacts)
- End-to-end scenarios through demo applications
- Visual verification through ASCII and Swing rendering

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
- **`SkiResortTycoonGUI`**: Main Swing application with integrated rendering

### Shared Utilities
- **`GameConstants`**: Centralized configuration values
- **`Position`**: Coordinate system with distance calculations

## 📈 Performance Considerations

- **Efficient Rendering**: Viewport culling ensures only visible tiles are drawn
- **Memory Optimization**: Immutable value objects reduce garbage collection
- **Scalable Architecture**: Components handle large resorts (100x100+ mountains)
- **Optimized Graphics**: Double-buffered Swing rendering with anti-aliasing

## 🤝 Contributing

The project architecture supports easy extension:

1. **Add New Components**: Follow the established core/visual/test pattern
2. **Enhance Graphics**: Extend the Swing rendering system
3. **Add Game Features**: Build on the solid financial and guest foundations
4. **Improve Testing**: Maintain the comprehensive test coverage

## 📝 Version History

### v1.0.0-SNAPSHOT (Current)
- ✅ Complete Mountain component with terrain generation and validation
- ✅ Advanced Guest system with AI and satisfaction tracking
- ✅ Comprehensive Financial system with loans and dynamic pricing
- ✅ Professional Swing GUI with isometric rendering
- ✅ Advanced camera system with smooth controls
- ✅ Comprehensive test suite (59 test cases)
- ✅ Multiple demo applications showcasing features
- ✅ ASCII rendering for debugging and console use
- ✅ Clean architecture with no redundancy

### Upcoming v1.1.0
- 🚧 Enhanced UI menus and construction interfaces
- 🚧 Weather system integration
- 🚧 Save/load functionality
- 🚧 Guest animation and movement visualization

## 🎯 Project Goals

1. **Learning Platform**: Demonstrate clean architecture and design patterns in Java
2. **Scalable Foundation**: Build a system that can grow into a full tycoon game  
3. **Professional Graphics**: Showcase Java Swing capabilities for modern game development
4. **Test Excellence**: Maintain comprehensive test coverage as complexity grows
5. **Clean Code**: Prioritize readability and maintainability over features 