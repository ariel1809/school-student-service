# ---- build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY common-lib common-lib
COPY student-service/pom.xml student-service/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY api-gateway/pom.xml api-gateway/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml
COPY user-service/pom.xml user-service/pom.xml
COPY config-service/pom.xml config-service/pom.xml
COPY teacher-service/pom.xml teacher-service/pom.xml
COPY billing-service/pom.xml billing-service/pom.xml
COPY file-service/pom.xml file-service/pom.xml
RUN mvn -pl student-service -am -B -DskipTests dependency:go-offline || true
COPY student-service/src student-service/src
RUN mvn -pl student-service -am -B -DskipTests package

# ---- run stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/student-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
