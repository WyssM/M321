# mqtt-sensor
FROM maven:3.8.4-openjdk-17 AS build
#WORKDIR /app
COPY pom.xml /tmp
COPY src /tmp/src
RUN mvn -f /tmp/pom.xml clean package -DskipTests

FROM openjdk:17
WORKDIR /app
COPY --from=build /tmp/target/my-app-1.0-SNAPSHOT.jar mqtt-sensor.jar