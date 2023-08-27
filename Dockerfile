# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/yahoo.finace.scraper-0.0.1-SNAPSHOT.jar /app/yahoo.finace.scraper-0.0.1-SNAPSHOT.jar

# Expose port 8080 to the outside world
EXPOSE 8080

# Specify the command to run your application
CMD ["java", "-jar", "yahoo.finace.scraper-0.0.1-SNAPSHOT.jar"]