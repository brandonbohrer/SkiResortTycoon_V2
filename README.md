# 🎿 Ski Resort Tycoon

A Java-based ski resort management game inspired by RollerCoaster Tycoon, built with clean architecture and scalability in mind.

## 🌟 Project Overview

Ski Resort Tycoon is a management simulation game where players build and operate a ski resort. The project focuses on:

- **Clean Architecture**: Separation of concerns with core business logic isolated from UI and infrastructure
- **Component-Based Design**: Each major feature (Mountain, Guests, Finances) is a self-contained component
- **Scalability**: Modular structure allows easy addition of new features without breaking existing functionality
- **Test-Driven Development**: Comprehensive test coverage ensures reliability as the codebase grows

## 🏗️ Architecture

### Component Structure
Each major game feature follows a consistent structure:
```
components/
├── mountain/
│   ├── core/          # Business logic and entities
│   ├── visual/        # Rendering and presentation
│   └── tests/         # Component-specific tests
├── guests/            # Guest management (planned)
├── finances/          # Financial system (planned)
└── infrastructure/    # Buildings and facilities (planned)
```

### Clean Architecture Layers
- **Core**: Domain entities and business rules
- **Components**: Feature-specific modules
- **Shared**: Common utilities and value objects
- **App**: Application entry point and configuration

## 🚀 Current Features

### Mountain Component ✅
- **Dynamic Terrain Generation**: Procedural mountain elevation with realistic slopes
- **Ski Slope Management**: Build slopes with different difficulty levels (Beginner, Intermediate, Advanced, Expert)
- **Ski Lift System**: Multiple lift types (Chairlift, Gondola, T-Bar, Magic Carpet) with capacity management
- **Validation System**: Ensures slopes go downhill and lifts go uphill based on terrain elevation
- **Visual Rendering**: ASCII-based mountain view showing terrain, slopes, and lifts
- **Appeal System**: Dynamic scoring for attractions based on conditions and crowding

### Technical Features
- **Maven Build System**: Professional project structure with dependency management
- **JUnit 5 Testing**: Comprehensive test suite with 11+ test cases
- **Java 17**: Modern Java features and best practices
- **Logging**: SLF4J with Logback for proper logging infrastructure
- **JSON Support**: Jackson for future save/load functionality

## 🎮 Demo

Run the interactive demo to see the Mountain component in action:

```bash
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonDemo"
```

The demo showcases:
- Mountain terrain generation
- Building multiple slopes and lifts
- Visual representation of the resort
- Financial calculations
- Appeal scoring system
- Guest capacity management

## 🛠️ Getting Started

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

### Running Tests
```bash
mvn test
```

### Running the Demo
```bash
mvn compile exec:java -Dexec.mainClass="com.skiresort.app.SkiResortTycoonDemo"
```

## 📊 Example Output

```
🎿 Welcome to Ski Resort Tycoon! 🎿

🏔️  Mountain View with Infrastructure:
=== Alpine Paradise ===
Size: 25x25 | Slopes: 3 | Lifts: 3

++*****^^^^^^^^^^^*****++
++*****^^^^^^^^^^^*****++
++******^^^^^^^^^******++
+++********^S^****L***+++
++++********S***S*L**++++
+++++***S*L*S***S*L*+++++
++++++++S*L*S***S+L++++++
--------S-L-X---S-L------
........S.L.....S.L......

💰 Financial Summary:
  Total Slope Investment: $37,000
  Total Lift Investment: $115,000
  Total Resort Investment: $152,000
```

## 🔧 Core Classes

### Mountain Component
- **`Mountain`**: Core terrain and infrastructure management
- **`Slope`**: Ski slope entities with difficulty levels and appeal scoring
- **`Lift`**: Ski lift entities with capacity and throughput management
- **`Position`**: Immutable 2D coordinate system
- **`MountainRenderer`**: ASCII-based visualization system

### Shared Utilities
- **`GameConstants`**: Centralized configuration values
- **`Position`**: Coordinate system with distance calculations

## 🧪 Testing

The project includes comprehensive tests covering:
- Mountain terrain generation and validation
- Slope and lift positioning rules
- Elevation calculations and boundary checking
- Appeal scoring algorithms
- Capacity management systems

**Test Coverage**: 11 test cases with 100% pass rate

## 🚧 Planned Features

### Guest System
- Individual guest entities with preferences and satisfaction
- Skill-based slope selection (beginners prefer easier slopes)
- Dynamic satisfaction based on wait times, facilities, and weather
- Guest pathfinding and movement simulation

### Financial System
- Revenue from lift tickets and slope usage
- Operating costs (maintenance, staff, utilities)
- Loan system for expansion funding
- Dynamic pricing based on demand and season

### Weather & Seasons
- Snow quality affecting slope conditions
- Weather events (storms, powder days)
- Seasonal changes affecting guest flow
- Snowmaking systems for poor conditions

### Infrastructure
- Lodge and restaurant buildings
- Rental shops and ski schools
- Staff management (lift operators, patrol, maintenance)
- Parking and transportation systems

### Advanced Features
- Save/load game functionality
- Multiple difficulty levels
- Scenario-based challenges
- Advanced graphics (JavaFX integration)

## 🏛️ Design Principles

### Scalability First
- **Modular Components**: Each feature is isolated and can be developed independently
- **Interface-Based Design**: Easy to swap implementations or add new features
- **Event-Driven Architecture**: Loose coupling between systems
- **Test Coverage**: Every feature has comprehensive tests before implementation

### Clean Code
- **Single Responsibility**: Each class has one clear purpose
- **Immutable Value Objects**: Position and other data structures are immutable
- **Descriptive Naming**: Clear, business-domain focused naming
- **Documentation**: Comprehensive JavaDoc and README documentation

### Real-World Inspiration
- **Authentic Ski Resort Operations**: Based on actual ski resort management principles
- **Realistic Economics**: Financial modeling based on real ski industry data
- **Guest Behavior**: Psychology-based satisfaction and preference systems

## 📈 Performance Considerations

- **Efficient Elevation Storage**: 2D array for O(1) elevation lookups
- **Lazy Loading**: Components only initialize when needed
- **Memory Optimization**: Immutable objects reduce memory overhead
- **Scalable Rendering**: ASCII rendering scales to any mountain size

## 🤝 Contributing

The project is designed for easy extension:

1. **Add New Components**: Follow the established pattern in `components/`
2. **Extend Existing Features**: All core classes are designed for inheritance
3. **Add Tests**: Maintain the test-first approach
4. **Update Documentation**: Keep README and JavaDoc current

## 📝 Version History

### v1.0.0-SNAPSHOT (Current)
- ✅ Complete Mountain component with terrain generation
- ✅ Slope management with difficulty levels and validation
- ✅ Lift system with multiple types and capacity management
- ✅ ASCII-based visual rendering system
- ✅ Comprehensive test suite (11 test cases)
- ✅ Professional Maven project structure
- ✅ Interactive demo application

### Upcoming v1.1.0
- 🚧 Guest system with individual entities and satisfaction
- 🚧 Basic financial system with revenue and costs
- 🚧 Weather effects on operations

## 🎯 Project Goals

1. **Learning Platform**: Demonstrate clean architecture and design patterns in Java
2. **Extensible Foundation**: Create a solid base for a full ski resort simulation
3. **Professional Standards**: Use industry best practices for testing, documentation, and code organization
4. **Fun Factor**: Build an engaging and realistic ski resort management experience

---

**Built with ❤️ and ☕ by the Ski Resort Tycoon team**

*"Every expert skier started on the bunny hill - and every great codebase started with a solid foundation!"* 