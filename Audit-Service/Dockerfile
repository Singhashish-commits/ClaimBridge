# Stage 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# Cache dependencies first (makes future builds much faster)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
# We skip tests here to speed up the Docker build
RUN mvn clean package -DskipTests

# Stage 2: Create the lightweight runtime image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the generated JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8087

# Boot up the application
ENTRYPOINT ["java", "-jar", "app.jar"]