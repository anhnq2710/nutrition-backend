# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml và download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app

# Install native libraries required by ONNX Runtime
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    libgomp1 \
    && rm -rf /var/lib/apt/lists/*

# Copy file jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]