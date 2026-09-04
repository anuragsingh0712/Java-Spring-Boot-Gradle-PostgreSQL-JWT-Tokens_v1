.PHONY: build run test clean docker-up docker-down

build:
	./gradlew compileJava -q

test:
	./gradlew test -q

package:
	./gradlew bootJar -q

run: package
	SERVER_PORT=$${SERVER_PORT:-20383} bash start.sh

clean:
	./gradlew clean -q

docker-up:
	docker compose up --build -d

docker-down:
	docker compose down
