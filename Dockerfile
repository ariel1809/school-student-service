# Dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiez les fichiers
COPY mvn-settings.template.xml /tmp/settings.template.xml
COPY pom.xml .
COPY src ./src

# Build args pour recevoir les secrets de Fly.io
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

# Générer settings.xml en remplaçant les placeholders par les vraies valeurs
# Puis exécuter Maven avec le fichier généré
RUN sed -e "s|__GITHUB_USERNAME__|${GITHUB_ACTOR}|g" \
        -e "s|__GITHUB_TOKEN__|${GITHUB_TOKEN}|g" \
        /tmp/settings.template.xml > /root/.m2/settings.xml && \
    rm /tmp/settings.template.xml && \
    mvn -s /root/.m2/settings.xml --batch-mode package -DskipTests

# Stage runtime minimal
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
