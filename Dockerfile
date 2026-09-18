# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn package -DskipTests


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Wildcard - works regardless of artifactId, no manual renaming needed per project
COPY --from=build /app/target/app.jar ./app.jar

EXPOSE 8484

ENTRYPOINT ["java", "-jar", "app.jar"]