# Stage 1: Build stage using Maven and Temurin JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY app/pom.xml .
COPY app/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal runtime environment using Temurin JRE 17
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
