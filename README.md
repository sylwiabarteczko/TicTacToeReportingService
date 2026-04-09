# TicTacToeReportingService

A standalone Spring Boot service that collects and exposes game statistics
by consuming events from a Kafka topic published by the main TicTacToe application.

## Architecture

Listens to the `tictactoe.events` Kafka topic and processes the following events:
- `USER_REGISTERED` – increments registered user count
- `GAME_CREATED` – increments game count, tracks AI games
- `MOVE_MADE` – tracks moves per game
- `GAME_FINISHED` – increments finished game count
- `AI_ERROR` – tracks AI failures

Statistics are persisted in a separate PostgreSQL database and exposed via REST API.

## Requirements

- Java 21
- Maven
- Docker (for PostgreSQL, Kafka, Zookeeper)
- Main TicTacToe application running and publishing events

## Setup

### 1. Start infrastructure (Docker)
Make sure Docker is running with Kafka and PostgreSQL:
```bash
cd TicTacToe-Infrastructure
docker-compose up -d
```

Starts:
- PostgreSQL (reporting) on port `5435`
- Kafka on port `9092`
- Zookeeper on port `2181`

### 2. Configure application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5435/reporting
spring.datasource.username=reporting
spring.datasource.password=your_password

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=tictactoe-reporting
```

### 3. Run the service
```bash
mvn spring-boot:run
```

Available at: `http://localhost:8081`

## REST API

| Endpoint | Description |
|----------|-------------|
| `GET /summary` | Returns overall statistics |
| `GET /abandoned` | Returns count of abandoned games |

## Database

Uses Flyway for schema migration. Tables are created automatically on first run:

| Table | Description |
|-------|-------------|
| `reporting_users` | Registered users with timestamps |
| `reporting_games` | Games with creation time and status |
| `reporting_moves` | Individual moves with timestamps |
| `reporting_ai_errors` | AI errors with messages and timestamps |

## Screenshots

| | |
|:---:|---|
| **Registration page** | <img src="https://raw.githubusercontent.com/sylwiabarteczko/TicTacToe/auto-ai/web/Register.png" width="400"/> |
| **Login page** | <img src="https://raw.githubusercontent.com/sylwiabarteczko/TicTacToe/auto-ai/web/Login.png" width="400"/> |
| **Game board** | <img src="https://raw.githubusercontent.com/sylwiabarteczko/TicTacToe/auto-ai/web/Game.png" width="400"/> |