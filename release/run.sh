#!/bin/bash

echo "Starting LSF Server..."

# Create logs directory if it doesn't exist
if [ ! -d "logs" ]; then
    mkdir -p logs
fi

# Set Java options
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

# Set the config location
SPRING_CONFIG_LOCATION="file:./config/"

# Run the application
java $JAVA_OPTS -jar lsf-server.jar --spring.config.location=$SPRING_CONFIG_LOCATION

# Exit with the same exit code as the Java process
exit $? 