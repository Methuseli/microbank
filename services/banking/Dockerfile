# Use Maven to build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the project files into the container
COPY . .

# Build the Spring Boot JAR
RUN mvn clean package -DskipTests

# Use a lightweight JDK image for running the app
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy only the built JAR from the previous stage
COPY --from=build /app/target/banking-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]