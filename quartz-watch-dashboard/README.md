# Quartz Monitor Dashboard

A Spring Boot based monitoring dashboard for Quartz scheduler.

## Features

- Monitor Quartz jobs and triggers
- Execute, pause, resume, and delete jobs
- Add and manage triggers
- RESTful API with proper validation and error handling

## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

## Getting Started

### 1. Build the project

```bash
mvn clean install
```

### 2. Run the application

```bash
mvn spring-boot:run
```

Or run the main class `QuartzMonitorApplication` directly from your IDE.

### 3. Access the application

The application will start on port 8080 by default.

Health check: http://localhost:8080/api/health

## API Endpoints

### Job Management
- `GET /api/job/list` - Get paginated job list
- `POST /api/job/start?uuid={uuid}` - Execute a job
- `DELETE /api/job/delete?uuid={uuid}` - Delete a job  
- `POST /api/job/pause?uuid={uuid}` - Pause a job
- `POST /api/job/resume?uuid={uuid}` - Resume a job
- `GET /api/job/types` - Get available job types
- `POST /api/job/add` - Add a new job

### Trigger Management
- `GET /api/trigger/list?jobId={jobId}` - Get triggers for a job
- `POST /api/trigger/add` - Add a new trigger
- `DELETE /api/trigger/delete?uuid={uuid}` - Delete a trigger

### System
- `GET /api/health` - Health check
- `GET /api/info` - Application information

## Configuration

Configuration can be modified in `src/main/resources/application.yml`:

- Server port: `server.port`
- Logging levels: `logging.level`
- Application name: `spring.application.name`

## Architecture

The application follows Spring Boot best practices:

- **Controllers**: RESTful endpoints with validation
- **Services**: Business logic layer
- **DTOs**: Request/Response objects with validation
- **Exception Handling**: Global exception handler for consistent error responses
- **Response Wrapping**: Automatic response wrapping with ApiResponse

## Development

The project uses Spring Boot DevTools for hot reloading during development.

### Code Structure

```
src/main/java/com/quartz/monitor/
├── controller/          # REST controllers
├── service/            # Business services
├── config/             # Configuration classes
│   └── advice/         # AOP advice (exception handling, response wrapping)
├── core/               # Core Quartz integration
├── vo/                 # Value objects
│   ├── request/        # Request DTOs
│   └── response/       # Response DTOs
├── validation/         # Custom validators
└── QuartzMonitorApplication.java  # Main application class
```

## Testing

Run tests with:

```bash
mvn test
```

## Troubleshooting

1. **Port already in use**: Change the port in `application.yml`
2. **Quartz instance not configured**: Make sure to configure a Quartz instance before using job/trigger endpoints
3. **Validation errors**: Check the request payload matches the required format 