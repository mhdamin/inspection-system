# ========================================
# FleetGuard Backend - Multi-stage Dockerfile
# ========================================
# This Dockerfile uses multi-stage builds to:
# 1. Build the application with Maven (builder stage)
# 2. Create a minimal runtime image with JRE only
# Result: ~200MB final image vs ~600MB single-stage
# ========================================

# ========================================
# Stage 1: Build Stage
# ========================================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

LABEL stage=builder
LABEL maintainer="FleetGuard Team"

# Set working directory
WORKDIR /app

# Copy Maven configuration first (for layer caching)
# This layer will be cached unless pom.xml changes
COPY pom.xml .

# Download dependencies offline (cached layer)
# This significantly speeds up subsequent builds
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
# -DskipTests: Skip tests in Docker build (run separately in CI/CD)
# -B: Batch mode (non-interactive)
RUN mvn clean package -DskipTests -B

# ========================================
# Stage 2: Runtime Stage
# ========================================
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="FleetGuard Team"
LABEL description="FleetGuard Vehicle Inspection System Backend"
LABEL version="1.0.0"

# Install additional tools for health checks
RUN apk add --no-cache curl wget

# Create application directory
WORKDIR /app

# Create a non-root user for security
# Running as root in containers is a security risk
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to spring user
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port 8080
EXPOSE 8080

# Health check
# This allows Docker/Kubernetes to monitor application health
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# JVM options for containerized environments
ENV JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom -Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
