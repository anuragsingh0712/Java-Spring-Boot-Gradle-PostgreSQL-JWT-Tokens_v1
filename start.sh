#!/usr/bin/env bash
set -e

SERVER_PORT="${SERVER_PORT:-26787}"

./gradlew bootJar -q

java -jar build/libs/*.jar --server.port="$SERVER_PORT"
