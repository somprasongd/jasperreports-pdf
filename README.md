# JasperReports Generator

## How to build Docker image

1. Build Spring Boot Application:

    ```bash
    ./mvnw clean package -DskipTests
    ```

2. Build the Docker Image:

    ```bash
    docker build -t your-docker-image-name .
    ```
