# Step 1: Use OpenJDK image
FROM openjdk:17-jdk-slim as base

# Step 2: Set working directory inside the container
WORKDIR /app

# Step 3: Copy the JAR file (latest version) into the container
COPY target/*.jar app.jar

# Step 4: Expose the application port
EXPOSE 8080

# Step 5: Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
