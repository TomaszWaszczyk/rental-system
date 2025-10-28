# Car Rental System

```
Please design and implement a simulated Car Rental system using object-oriented principles.
Requirements
• The system should allow reservation of a car of a given type at a desired date and time for a given
number of days.
• There are 3 types of cars (Sedan, SUV and van).
• The number of cars of each type is limited.
• Use unit tests to prove the system satisfies the requirements. 
```

## Building and Running

### Prerequisites

- Java 17+
- Maven 3.6+

### Build Commands

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Run the demo application
mvn exec:java -Dexec.mainClass="com.waszczyk.Main"

```

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CarRentalServiceTest
```

## Gaps/deficiencies should be known & understood

- Add exceptions handling
- Better ID generation
- Add logger
