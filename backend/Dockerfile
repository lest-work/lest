# Multi-stage Dockerfile for LEST backend microservices
# Usage: docker build --build-arg MODULE=lest-auth -t lest-auth:v1.0.0 .
ARG MODULE=lest-auth

# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk-noble AS builder

ARG MODULE
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .
COPY buildSrc/ buildSrc/
COPY lest-common/ lest-common/
COPY lest-modules/ lest-modules/

RUN chmod +x gradlew && \
    ./gradlew :lest-modules:${MODULE}:bootJar --no-daemon -q

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre-noble

ARG MODULE
WORKDIR /app

RUN groupadd -r appuser && useradd -r -g appuser appuser

COPY --from=builder /workspace/lest-modules/${MODULE}/build/libs/*.jar app.jar

RUN chown appuser:appuser app.jar
USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -XX:+UseG1GC \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
