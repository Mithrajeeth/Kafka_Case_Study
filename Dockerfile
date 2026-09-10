# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/kafka-ops-1.0-SNAPSHOT.jar ./kafka-ops.jar
ENTRYPOINT ["java", "-jar", "kafka-ops.jar"]