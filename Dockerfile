# Use an official OpenJDK 17 base image
FROM eclipse-temurin:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the Spring Batch application JAR file into the container
COPY target/springbatchdemo-0.0.1-SNAPSHOT.jar app.jar

# Expose any required ports (if needed, for monitoring or metrics)
# EXPOSE 8080

# Command to run the batch job
CMD ["java", "-jar", "app.jar"]
