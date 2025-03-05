# Kafka Microservice

This project is a Spring Boot application that serves as a Kafka microservice to track all changes. It includes a Kafka producer and consumer to send and receive messages from a Kafka topic.

## Project Structure

```
kafka-microservice
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── kafkams
│   │   │               ├── KafkaMicroserviceApplication.java
│   │   │               ├── config
│   │   │               │   └── KafkaConfig.java
│   │   │               ├── consumer
│   │   │               │   └── KafkaConsumer.java
│   │   │               └── producer
│   │   │                   └── KafkaProducer.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── logback-spring.xml
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── kafkams
│                       └── KafkaMicroserviceApplicationTests.java
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Setup Instructions

1. **Clone the repository:**
   ```
   git clone <repository-url>
   cd kafka-microservice
   ```

2. **Build the project:**
   ```
   ./mvnw clean install
   ```

3. **Run the application:**
   ```
   ./mvnw spring-boot:run
   ```

## Usage

- The application listens for messages on a specified Kafka topic and processes them using the `KafkaConsumer`.
- You can send messages to the Kafka topic using the `KafkaProducer`.

## Configuration

- Kafka settings can be configured in `src/main/resources/application.properties`.
- Logging configuration is specified in `src/main/resources/logback-spring.xml`.

## Testing

- Unit tests for the application can be found in `src/test/java/com/example/kafkams/KafkaMicroserviceApplicationTests.java`.
- Run the tests using:
  ```
  ./mvnw test
  ```

## Dependencies

This project uses Maven for dependency management. The required dependencies are specified in the `pom.xml` file.