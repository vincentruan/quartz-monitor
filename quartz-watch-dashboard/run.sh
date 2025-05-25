#!/bin/bash

# Quartz Monitor Dashboard startup script

echo "Starting Quartz Monitor Dashboard..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null
then
    echo "Maven is not installed. Please install Maven first."
    exit 1
fi

# Clean and package the application
echo "Building the application..."
mvn clean package -DskipTests

# Check if build was successful
if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

# Run the application
echo "Starting the application..."
java -jar target/quartz-watch-dashboard-0.0.1-SNAPSHOT.jar 