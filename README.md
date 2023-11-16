# JasperReports Generator

## How to build Docker image

1. Build Spring Boot Application:

    ```bash
    ./mvnw clean package -DskipTests
    ```

2. Build the Docker Image:

    ```bash
    docker build -t your-docker-image-name .
   
    # multiplatform
    # docker buildx create --use 
    # docker buildx build --push --platform linux/amd64,linux/arm64 -t your-docker-image-name:latest .
    ```

## How to use

```bash
curl --location 'http://localhost:8080/api/v1/jasper/generate' \
--header 'Content-Type: application/json' \
--data '{
    "datasource": "opd",
    "mainReport": {
        "name": "test",
        "url": "test.jrxml",
        "modified_at": 1
    },
    "parameters": [
        {
            "name": "id",
            "type": "string",
            "value": "1234"
        }
    ]
}'
```
