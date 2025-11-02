# ---- Stage 1: Build ----
FROM gradle:8.10-jdk21-alpine AS build

WORKDIR /micro-ops

COPY settings.gradle ./
RUN gradle dependencies --no-daemon || true

# Copy everything (multi-module build)
COPY . .

# Build only the user-service module (skip tests)
RUN gradle :user-service:build -x test --no-daemon

# ---- Stage 2: Runtime ----
FROM alpine:3.20 AS runtime
WORKDIR /micro-ops

# Install OpenJDK 21 JRE and Create non-root user and group
RUN apk add --no-cache openjdk21-jre-headless && addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the built JAR file from the build stage
COPY --chown=appuser:appgroup --from=build /micro-ops/user-service/build/libs/*.jar /micro-ops/user-service.jar
COPY --chown=appuser:appgroup --from=build /micro-ops/user-service/src/main/resources/application.yml /micro-ops/application.yml

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
ENV SPRING_CONFIG_LOCATION=/micro-ops/application.yml
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 80

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /micro-ops/user-service.jar"]