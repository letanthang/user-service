run:
	mvn clean install
	java -jar target/user-service-1.0.jar

build:
	docker build \
	--build-arg MAVEN_IMAGE=maven:3.9.6-eclipse-temurin-17 \
	--build-arg JRE_IMAGE=eclipse-temurin:17-jre \
	-t user-service:latest .
up:
	docker compose up