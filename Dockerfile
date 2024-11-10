FROM hivemq/hivemq-edge:4.4.0

# Expose the port used by MQTT (default: 1883)
EXPOSE 1883

# Run HiveMQ Edge
CMD ["./bin/hivemq-edge", "--bind-address", "0.0.0.0"]
