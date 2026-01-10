#!/bin/bash

set -e  # Dừng script nếu có lỗi

echo "=================================="
echo "Khởi động Nacos + MySQL (standalone)"
echo "=================================="

# Kiểm tra nếu network app-network chưa tồn tại thì tạo mới
if ! docker network inspect app-network >/dev/null 2>&1; then
  echo "Tạo network app-network..."
  docker network create app-network
fi

# 1. Khởi động MySQL (nacosDB)
echo "Khởi động MySQL (nacosDB)..."
docker run -d \
  --name nacosDB \
  --restart unless-stopped \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=nacos \
  -e MYSQL_USER=nacos \
  -e MYSQL_PASSWORD=nacos \
  -v "$(pwd)/nacos/nacosDB":/docker-entrypoint-initdb.d \
  --network app-network \
  mysql:8.0 || { echo "Lỗi khi khởi động MySQL"; exit 1; }

echo "MySQL đang chạy..."

# Chờ MySQL sẵn sàng (tùy chọn, tránh Nacos start quá sớm)
echo "Chờ MySQL sẵn sàng (khoảng 20s)..."
sleep 20

# 2. Khởi động Nacos server
echo "Khởi động Nacos server..."
docker run -d \
  --name nacos-standalone-derby \
  --hostname nacos \
  --restart unless-stopped \
  -p 8848:8848 \
  -p 9848:9848 \
  -p 8081:8080 \
  -e MODE=standalone \
  -e SPRING_DATASOURCE_PLATFORM=mysql \
  -e MYSQL_SERVICE_HOST=nacosDB \
  -e MYSQL_SERVICE_DB_NAME=nacos \
  -e MYSQL_SERVICE_USER=nacos \
  -e MYSQL_SERVICE_PASSWORD=nacos \
  -e MYSQL_SERVICE_DB_PARAM="characterEncoding=utf8&connectTimeout=10000&socketTimeout=30000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true" \
  -e NACOS_AUTH_ENABLE=true \
  -e NACOS_AUTH_TOKEN=SecretKey012345678901234567890123456789012345678901234567890123456789 \
  -e NACOS_AUTH_IDENTITY_KEY=serverIdentity \
  -e NACOS_AUTH_IDENTITY_VALUE=security \
  -e NACOS_AUTH_USERNAME=nacos \
  -e NACOS_AUTH_PASSWORD=phuong \
  --network app-network \
  nacos/nacos-server:v3.1.1 || { echo "Lỗi khi khởi động Nacos"; exit 1; }

echo "=================================="
echo "Hoàn tất! Nacos đang chạy"
echo "Truy cập console: http://localhost:8848/nacos"
echo "Username: nacos"
echo "Password: phuong"
echo "=================================="
echo "Xem log Nacos: docker logs -f nacos-standalone-derby"
echo "Dừng 2 service: docker stop nacos-standalone-derby nacosDB"
echo "Xóa hoàn toàn (reset DB): docker rm -f nacos-standalone-derby nacosDB && docker volume prune -f"