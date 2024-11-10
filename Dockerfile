FROM openjdk:11-jre-slim

# Install dependencies
RUN apt-get update && apt-get install -y curl unzip

# Download and install HiveMQ Edge (example for zip)
RUN curl -L https://www.hivemq.com/downloads/hivemq-edge-4.4.0.zip -o hivemq-edge.zip
RUN unzip hivemq-edge.zip && rm hivemq-edge.zip

# Set working directory
WORKDIR /hivemq-edge

# Expose the port used by MQTT (default: 1883)
EXPOSE 1883

# Start HiveMQ Edge
CMD ["./bin/hivemq-edge", "--bind-address", "0.0.0.0"]
