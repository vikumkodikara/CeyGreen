# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
# Resolve dependencies in a separate layer so rebuilds are faster and more reliable.
RUN ./mvnw -B -ntp dependency:go-offline -DskipTests || true

COPY src ./src
RUN ./mvnw -B -ntp package -DskipTests

FROM eclipse-temurin:17-jre AS runtime
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
RUN useradd --system --create-home --shell /usr/sbin/nologin ceygreen && chown -R ceygreen:ceygreen /app
COPY --from=build /build/target/ecommerce-service-*.jar app.jar
USER ceygreen
EXPOSE 8084
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
