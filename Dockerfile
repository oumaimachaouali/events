FROM maven:3.8.1-openjdk-17-slim AS builder
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean install -DskipTests
FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/target/*.jar ./app.jar
EXPOSE 8091
CMD ["java", "-jar", "app.jar"]