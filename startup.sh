#!/bin/bash

# Bring down any existing Docker containers
echo "Stopping existing Docker containers..."
docker compose down

# Start up Docker containers
echo "Starting Docker containers..."
docker compose up -d

# Wait for a few seconds to ensure containers are ready
echo "Waiting for containers to be ready..."
sleep 10

# Start the Store Management application
echo "Starting Store Management application..."
./mvnw spring-boot:run
