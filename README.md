Asteroid Tracker

A Spring Boot REST API that integrates with NASA Near Earth Object (NEO) APIs to retrieve asteroid data and return the 10 closest asteroids to Earth within a given date range.

Features

* Validate date range input (maximum 7 days)
* Retrieve asteroid data from NASA Neo Feed API
* Aggregate asteroids from multiple dates
* Sort asteroids by closest miss distance
* Return only the 10 closest asteroids
* Return simplified response payload

Technology Stack

* Java 17
* Spring Boot 3
* Gradle
* JUnit 5
* Mockito
* Lombok

Prerequisites

* Java 17
* Gradle (or use Gradle Wrapper)

Configuration

Set NASA API key as an environment variable:

export NASA_API_KEY=YOUR_API_KEY

If not provided, the application uses NASA’s DEMO_KEY.

Build

./gradlew clean build

Run

./gradlew bootRun

Application will start on:

http://localhost:8080

Run Tests

./gradlew test

API Endpoint

Get Closest Asteroids

GET /api/asteroids

Query Parameters

Parameter	Type	Format	Required
startDate	Date	YYYY-MM-DD	Yes
endDate	Date	YYYY-MM-DD	Yes

Example Request

GET /api/asteroids?startDate=2025-01-01&endDate=2025-01-07

Example Response

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

Validation Rules

* Date format must be YYYY-MM-DD
* Date range must not exceed 7 days
* Start date must not be after end date

Error Response Example

{
"timestamp": "2025-01-01T10:00:00",
"status": 400,
"error": "Bad Request",
"message": "Date range must not be more than 7 days"
}

Note
* The Neo Feed endpoint is used as the primary source because it already contains all information required by the assignment.
* The Neo Lookup client is implemented and available for future enhancement when detailed asteroid information is required.