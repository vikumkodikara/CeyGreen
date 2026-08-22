# Stage 1: Build application with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B -ntp
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN useradd -u 1001 -r -s /bin/false ceygreen
USER ceygreen

COPY --from=build /app/target/sales-analytics-notification-service-1.0.0.jar app.jar

ENV PORT=8086
EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]

