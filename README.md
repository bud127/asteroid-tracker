# Asteroid Tracker

A Spring Boot REST API that integrates with NASA Near Earth Object (NEO) APIs to retrieve asteroid information and provide the closest asteroids to Earth within a given date range.

## Features

- Validate date range input with `YYYY-MM-DD` format
- Restrict date range to a maximum of 7 days
- Retrieve asteroid data from NASA Neo Feed API
- Retrieve asteroid details from NASA Neo Lookup API
- Aggregate asteroids from multiple dates into a single collection
- Sort asteroids by closest miss distance
- Return only the 10 closest asteroids
- Return simplified response payload
- Global exception handling
- Configurable NASA API timeout

## Technology Stack

| Technology | Version |
|------------|---------|
| Java | 17 |
| Spring Boot | 3.x |
| Gradle | Latest |
| JUnit | 5 |
| Mockito | Latest |
| Lombok | Latest |

## Prerequisites

- Java 17 or later
- Gradle, or use the included Gradle Wrapper

## Configuration

Set NASA API key as an environment variable:

```bash
export NASA_API_KEY=YOUR_API_KEY
```

If the environment variable is not provided, the application uses NASA's `DEMO_KEY`.

### application.properties

```properties
spring.application.name=asteroid-tracker
nasa.api.base-url=https://api.nasa.gov
nasa.api.key=${NASA_API_KEY:DEMO_KEY}
nasa.api.connect-timeout=5000
nasa.api.read-timeout=10000
```

## Build

```bash
./gradlew clean build
```

## Run

```bash
./gradlew bootRun
```

Application will start on:

```text
http://localhost:8080
```

## Run Tests

```bash
./gradlew test
```

## API Documentation

### Get Closest Asteroids

Returns the 10 closest asteroids to Earth within the specified date range.

#### Endpoint

```http
GET /api/asteroids
```

#### Query Parameters

| Parameter | Type | Format | Required | Description |
|-----------|------|--------|----------|-------------|
| startDate | Date | YYYY-MM-DD | Yes | Start date of the search range |
| endDate | Date | YYYY-MM-DD | Yes | End date of the search range |

#### Example Request

```http
GET /api/asteroids?startDate=2025-01-01&endDate=2025-01-07
```

#### Example Response

```json
[
  {
    "id": "12345",
    "name": "Asteroid A",
    "missDistanceKm": 100000,
    "estimatedDiameterMinMeters": 10,
    "estimatedDiameterMaxMeters": 20,
    "potentiallyHazardous": false
  }
]
```

### Get Asteroid Detail

Returns detailed information for a specific asteroid using NASA Neo Lookup API.

#### Endpoint

```http
GET /api/asteroids/{id}
```

#### Path Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| id | String | Yes | NASA asteroid identifier |

#### Example Request

```http
GET /api/asteroids/3542519
```

#### Example Response

```json
{
  "id": "3542519",
  "name": "(2010 PK9)",
  "nasaJplUrl": "https://ssd.jpl.nasa.gov/tools/sbdb_lookup.html#/?sstr=3542519",
  "estimatedDiameterMinMeters": 269.457,
  "estimatedDiameterMaxMeters": 602.81,
  "potentiallyHazardous": false
}
```

## Validation Rules

- Date format must be `YYYY-MM-DD`
- Date range must not exceed 7 days
- Start date must not be after end date

## Error Responses

### 400 Bad Request

Returned when request parameters are invalid.

```json
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Date range must not be more than 7 days"
}
```

### 404 Not Found

Returned when the asteroid ID cannot be found from NASA Neo Lookup API.

```json
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Asteroid not found: 3542519"
}
```

### 502 Bad Gateway

Returned when NASA API fails, times out, or returns an incomplete response.

```json
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 502,
  "error": "Bad Gateway",
  "message": "Failed to retrieve data from NASA API"
}
```

### 500 Internal Server Error

Returned when an unexpected application error occurs.

```json
{
  "timestamp": "2025-01-01T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Unexpected error occurred"
}
```

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.budi.asteroid.tracker
│   │       ├── client
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       ├── exception
│   │       ├── service
│   │       └── validator
│   └── resources
│       └── application.properties
└── test
    └── java
        └── com.budi.asteroid.tracker
```

## Design Decisions

### Why use NASA Neo Feed API?

NASA Neo Feed API is used to retrieve asteroids within the requested date range. The application aggregates asteroids from multiple dates into a single collection, sorts them by closest miss distance, and returns the top 10 results.

### Why use NASA Neo Lookup API?

NASA Neo Lookup API is used for the asteroid detail endpoint. This keeps the list endpoint efficient by avoiding multiple lookup calls for every search request.

### Why only return selected fields?

NASA responses contain large payloads. The application maps only relevant attributes to keep the API response simple and focused.

## Error Handling Strategy

| Scenario | HTTP Status |
|----------|-------------|
| Invalid request parameter | 400 |
| Invalid date range | 400 |
| Asteroid not found | 404 |
| NASA API failure | 502 |
| NASA timeout | 502 |
| Incomplete NASA response | 502 |
| Unexpected application error | 500 |

## Assumptions

- NASA Neo Feed API is the source of asteroid search results.
- NASA Neo Lookup API is used only for asteroid detail retrieval.
- Incomplete or malformed NASA responses are treated as upstream failures.
- Only relevant attributes are exposed to consumers instead of returning NASA's full payload.
