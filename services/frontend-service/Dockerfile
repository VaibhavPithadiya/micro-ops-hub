FROM nginx:1.27-alpine

WORKDIR /usr/share/nginx/html

# Remove default files
RUN rm -rf ./*

# Copy static frontend-service files
COPY frontend-service/ ./

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup && \
    apk add --no-cache libcap && \
    setcap 'cap_net_bind_service=+ep' /usr/sbin/nginx && \
    chown -R appuser:appgroup \
        /usr/share/nginx/html \
        /var/cache/nginx \
        /var/run \
        /var/log/nginx \
        /etc/nginx/conf.d && \
    sed -i 's|pid\s*/run/nginx.pid;|pid /var/cache/nginx/nginx.pid;|' /etc/nginx/nginx.conf || \
    echo 'pid /var/cache/nginx/nginx.pid;' >> /etc/nginx/nginx.conf && \
    sed -i '/^user\s\+nginx;/d' /etc/nginx/nginx.conf

USER appuser

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
