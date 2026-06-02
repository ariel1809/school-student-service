# Dockerfile
# Étape 1 : Build avec Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiez d'abord settings.xml et pom.xml pour optimiser le cache Docker
COPY mvn-settings.xml /root/.m2/settings.xml
COPY pom.xml .
COPY src ./src

# Build avec Maven
# Les variables GITHUB_ACTOR/TOKEN sont injectées via ARG/ENV (plus simple que --mount=secret)
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
ENV GITHUB_ACTOR=${GITHUB_ACTOR}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

RUN mvn -s /root/.m2/settings.xml --batch-mode package -DskipTests

# Étape 2 : Image runtime minimale
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copiez uniquement le JAR built
COPY --from=build /app/target/*.jar app.jar

# Santé pour Fly.io (assurez-vous d'avoir spring-boot-starter-actuator)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
