# ============================================================
# MULTI-STAGE DOCKERFILE FOR CLOUD NOTIFICATION DISPATCHER
# Stage 1: Build & Package with Maven
# Stage 2: Distroless Lightweight JRE Runtime
# ============================================================

FROM maven:3.9.6-eclipse-temurin-23-alpine AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Distroless Production Image
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# Security: Non-root user execution
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /workspace/target/*.jar app.jar

EXPOSE 8086

ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
ENV CLOUD_SECURITY_API_KEY="vibe-cloud-secret-key-2026"

HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8086/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
