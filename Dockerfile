FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
EXPOSE 8080
RUN apk add ttf-dejavu
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
WORKDIR /app
RUN mkdir jaspers && mkdir jrxmls && mkdir images
VOLUME /app/jaspers
VOLUME /app/jrxmls
VOLUME /app/images
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar ./app.jar --spring.profiles.active=prod"]
# ENTRYPOINT ["java","${JAVA_OPTS}","-jar","./app.jar"]
# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","./app.jar"]