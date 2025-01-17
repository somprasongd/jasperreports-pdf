# JasperReports Generator

## How to build Docker image

1. Build Spring Boot Application:

    ```bash
    ./mvnw clean package -DskipTests
    ```

2. Build the Docker Image:

    ```bash
    docker build -t your-namespace/jasperreports-pdf:latest .
   
    # multiplatform
    # docker buildx create --use 
    # docker buildx build --push --platform linux/amd64,linux/arm64 -t your-namespace/jasperreports-pdf:latest .
    ```

## How to use

```bash
curl --location 'http://localhost:9090/api/v1/jasper/generate' \
--header 'Content-Type: application/json' \
--header 'sentry-trace: 7c6f8eae8f614f749ff5692f4568a5c7-98af56d21e1af55c-1' \
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
