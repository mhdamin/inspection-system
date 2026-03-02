# ========================================
# FleetGuard Frontend - Multi-stage Dockerfile
# ========================================
# This Dockerfile uses multi-stage builds to:
# 1. Build the React app with Node.js (builder stage)
# 2. Serve static files with Nginx (runtime stage)
# Result: ~50MB final image vs ~1GB with Node included
# ========================================

# ========================================
# Stage 1: Build Stage
# ========================================
FROM node:20-alpine AS builder

LABEL stage=builder
LABEL maintainer="FleetGuard Team"

# Set working directory
WORKDIR /app

# Copy package files first (for layer caching)
COPY package*.json ./

# Install dependencies
# Using npm install instead of npm ci due to package-lock.json sync issues
# --legacy-peer-deps to handle React 19 compatibility
RUN npm install --legacy-peer-deps

# Copy source code
COPY . .

# Build the application
# This creates optimized production build in /app/dist
RUN npm run build

# ========================================
# Stage 2: Runtime Stage (Nginx)
# ========================================
FROM nginx:1.25-alpine

LABEL maintainer="FleetGuard Team"
LABEL description="FleetGuard Vehicle Inspection System Frontend"
LABEL version="1.0.0"

# Remove default nginx configuration
RUN rm /etc/nginx/conf.d/default.conf

# Copy custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built files from builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Create nginx user (if not exists)
RUN addgroup -g 101 -S nginx || true && \
    adduser -S -D -H -u 101 -h /var/cache/nginx -s /sbin/nologin -G nginx -g nginx nginx || true

# Expose port 80
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/health || exit 1

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
