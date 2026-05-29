FROM eclipse-temurin:25-jre-noble

ARG MODULE=lest-auth
ARG SERVICE_PORT=8080

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser -d /app -s /bin/bash appuser

WORKDIR /app

# Copy target directory, then pick the jar via shell
COPY ${MODULE}/target/ /tmp/target/
RUN cp /tmp/target/*.jar app.jar 2>/dev/null || \
    cp /tmp/target/*-1.0.0.jar app.jar && \
    rm -rf /tmp/target && \
    mkdir -p /app/logs && \
    chown -R appuser:appuser /app

USER appuser

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

EXPOSE ${SERVICE_PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${SERVICE_PORT}"]
