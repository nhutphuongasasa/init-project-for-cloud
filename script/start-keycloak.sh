#!/bin/bash

set -e
GREEN='\033[0;32m'; 
YELLOW='\033[1;33m'; 
RED='\033[0;31m'; 
CYAN='\033[0;36m'; 
BLUE='\033[0;34m'; 
PURPLE='\033[0;35m'; 
NC='\033[0m'
BOLD='\033[1m'; 
DIM='\033[2m'
DOMAIN="keycloak.icuture.icu"
echo -e "${GREEN}=== KEYCLOAK 25.0.6 – FINAL VERSION ===${NC}"

echo -e "${YELLOW}Dọn container cũ...${NC}"
docker stop keycloak 2>/dev/null || true
docker rm keycloak 2>/dev/null || true

echo -e "${YELLOW}Cấu hình Nginx${NC}"
sudo tee /etc/nginx/sites-available/keycloak > /dev/null << EOF
server {
    listen 80;
    server_name $DOMAIN;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name $DOMAIN;

    ssl_certificate /etc/letsencrypt/live/$DOMAIN/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/$DOMAIN/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    client_max_body_size 50M;

    proxy_buffering off;
    proxy_request_buffering off;
    proxy_set_header Connection "";

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port 443;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}
EOF
sudo nginx -t && sudo systemctl reload nginx && echo -e "${GREEN}Nginx OK!${NC}"

echo -e "${YELLOW}Khởi động Keycloak 25.0.6 ${NC}"
docker run -d \
  --name keycloak \
  --network app-network \
  -p 127.0.0.1:8080:8080 \
  --restart unless-stopped \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
  -e KC_HOSTNAME=$DOMAIN \
  -e KC_PROXY=edge \
  -e KC_HTTP_ENABLED=true \
  -e KC_DB=postgres \
  -e KC_DB_URL="jdbc:postgresql://ep-orange-forest-a1cnwb51-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require" \
  -e KC_DB_USERNAME=neondb_owner \
  -e KC_DB_PASSWORD=npg_AYn3F4vBjXxg \
  -e RABBITMQ_HOST=fuji.lmq.cloudamqp.com \
  -e RABBITMQ_USER=tdteuzte \
  -e RABBITMQ_PASS=lIrc_ZdOdoQZ3haXShEgsl8EpOn2jnXG \
  -e RABBITMQ_VHOST=tdteuzte \
  quay.io/keycloak/keycloak:25.0.6 \
  start

echo -e "${YELLOW}Đang khởi tạo database & admin user... (80-100s lần đầu)${NC}"
sleep 8
for i in {1..14}; do
    echo -ne "${CYAN} ▰▰▰▰▰▰▰▰▰▰ ${i}0%${NC}\r"
    sleep 6
done
echo -e "${CYAN} ▰▰▰▰▰▰▰▰▰▰ 100%${NC}"

echo
echo -e "${BLUE}${BOLD}ĐANG KIỂM TRA HOÀN CHỈNH...${NC}"
sleep 2

echo -e "${CYAN}1. Kết nối HTTPS & OpenID metadata...${NC}"; sleep 1
if curl -f -k -s "https://$DOMAIN/realms/master/.well-known/openid-configuration" > /dev/null; then
    echo -e "   ${GREEN}HOÀN HẢO – 200 OK${NC}"
else
    echo -e "   ${RED}LỖI – Không phản hồi${NC}"; exit 1
fi

echo -e "${CYAN}2. Đăng nhập admin/admin123...${NC}"; sleep 1
TOKEN=$(curl -k -s -X POST "https://$DOMAIN/realms/master/protocol/openid-connect/token" \
  -d "client_id=security-admin-console" -d "username=admin" -d "password=admin123" -d "grant_type=password" --max-time 15 \
  | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
    echo -e "   ${GREEN}THÀNH CÔNG – Token đã nhận!${NC}"
else
    echo -e "   ${RED}THẤT BẠI – Sai user/pass hoặc server lỗi${NC}"; exit 1
fi

echo -e "${CYAN}3. Admin Console UI (không interrupt)...${NC}"; sleep 1
if curl -f -k -s --max-time 20 "https://$DOMAIN/js/admin.js" > /dev/null; then
    echo -e "   ${GREEN}MƯỢT NHƯ BƠ – Không bị interrupt${NC}"
else
    echo -e "   ${RED}CÓ VẤN ĐỀ – JS bị ngẽn${NC}"; exit 1
fi

echo -e "${PURPLE}${BOLD}"
echo "╔═══════════════════════════════════════════════════════════════════╗"
echo "║                      KEYCLOAK ĐÃ CHẠY HOÀN HẢO!                   ║"
echo "║                         https://$DOMAIN                         ║"
echo "╚═══════════════════════════════════════════════════════════════════╝${NC}"
