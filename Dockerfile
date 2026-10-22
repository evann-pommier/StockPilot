# syntax=docker/dockerfile:1

# --- 1. Build du frontend ---------------------------------------------------
FROM node:22-alpine AS frontend
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# --- 2. Build du backend (embarque le frontend comme ressources statiques) --
FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /app
COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src ./src
COPY --from=frontend /app/frontend/dist ./src/main/resources/static
RUN mvn -B -q package -DskipTests

# --- 3. Image d'exécution minimale ------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 stockpilot
COPY --from=backend /app/target/*.jar app.jar
USER stockpilot
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
