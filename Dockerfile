# Dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY mvn-settings.xml /root/.m2/settings.xml
COPY pom.xml .
COPY src ./src

# Build args pour recevoir les secrets de Fly.io
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

# Build Maven avec credentials passés via -D (plus fiable que ${env.*})
RUN mvn -s /root/.m2/settings.xml \
  -Dgithub.username=${GITHUB_ACTOR} \
  -Dgithub.token=${GITHUB_TOKEN} \
  --batch-mode package -DskipTests

# Stage runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
