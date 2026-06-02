# Dockerfile
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /app

# Copiez d'abord le settings.xml et le pom.xml pour optimiser le cache Docker
COPY mvn-settings.xml /root/.m2/settings.xml
COPY pom.xml .
COPY src ./src

# Build avec Maven en utilisant le settings.xml personnalisé
# Les variables d'environnement GITHUB_ACTOR/TOKEN seront injectées au build
RUN --mount=type=secret,id=GITHUB_TOKEN \
    GITHUB_ACTOR=placeholder \
    GITHUB_TOKEN=$(cat /run/secrets/GITHUB_TOKEN) \
    mvn -s /root/.m2/settings.xml --batch-mode package -DskipTests

# Stage production : image minimale
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copiez uniquement le JAR built depuis le stage précédent
COPY --from=build /app/target/*.jar app.jar

# Exposez le port par défaut de Spring Boot
EXPOSE 8080

# Health check pour Fly.io
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Démarrage de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
