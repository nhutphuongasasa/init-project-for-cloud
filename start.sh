#!/bin/bash

Kiểm tra network, nếu chưa có thì tạo
if ! docker network ls --format '{{.Name}}' | grep -w app-network > /dev/null; then
  echo "Network app-network chưa tồn tại, tạo mới..."
  docker network create app-network
else
  echo "Network app-network đã tồn tại."
fi

if [ "$(docker ps --format '{{.Names}}' | grep -w nacos-standalone-derby)" ]; then
    echo "Container nacos-standalone-derby đang chạy, không làm gì."
elif [ "$(docker ps -a --format '{{.Names}}' | grep -w nacos-standalone-derby)" ]; then
    echo "Container nacos-standalone-derby đã tồn tại nhưng đang dừng, khởi động lại..."
    docker start nacos-standalone-derby
else
 docker run --name nacos-standalone-derby \
  --network app-network \
  -e MODE=standalone \
  -e NACOS_AUTH_TOKEN=SecretKey012345678901234567890123456789012345678901234567890123456789 \
  -e NACOS_AUTH_IDENTITY_KEY=serverIdentity \
  -e NACOS_AUTH_IDENTITY_VALUE=security \
  -p 8081:8080 \
  -p 8848:8848 \
  -p 9848:9848 \
  -d nacos/nacos-server:latest
fi


# if [ "$(docker ps --format '{{.Names}}' | grep -w keycloak)" ]; then
#     echo "Container keycloak đang chạy, không làm gì."
# elif [ "$(docker ps -a --format '{{.Names}}' | grep -w keycloak)" ]; then
#     echo "Container keycloak đã tồn tại nhưng đang dừng, khởi động lại..."
#     docker start keycloak
# else
#   echo "Starting create keycloak"
#   docker run -d \
#     --name keycloak \
#     --network app-network \
#     -p 8080:8080 \
#     -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
#     -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin123 \
#     -e KC_HOSTNAME=auth.mycompany.com \
#     -e KC_HOSTNAME_STRICT=true \
#     -e KC_HOSTNAME_STRICT_HTTPS=true \
#     -e KC_HTTP_ENABLED=true \
#     -e KC_HEALTH_ENABLED=true \
#     -e KC_METRICS_ENABLED=true \
#     -e KC_LOG_LEVEL=INFO \
#     -e KEYCLOAK_ADMIN=admin \
#     -e KEYCLOAK_ADMIN_PASSWORD=admin123 \
#     -e KC_DB=postgres \
#     -e KC_DB_URL="jdbc:postgresql://ep-rough-mountain-a14gmwzf-pooler.ap-southeast-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require" \
#     -e KC_DB_USERNAME=neondb_owner \
#     -e KC_DB_PASSWORD=npg_MHCgWr1Ss9Ih \
#     -v $(pwd)/realms:/opt/keycloak/data/import:ro \
#     quay.io/keycloak/keycloak \
#     start-dev --import-realm
# fi

# TOTAL=45
# INTERVAL=1  # Cập nhật mỗi giây

# echo "🚀 Bắt đầu vòng lặp 60 giây..."
# echo

# for ((i=1; i<=TOTAL; i++)); do
#     # Tính phần trăm hoàn thành
#     percent=$(( i * 100 / TOTAL ))
    
#     # Tính thời gian còn lại
#     remaining=$(( TOTAL - i ))
    
#     # Tạo thanh tiến trình
#     bars=$(( percent / 2 ))  # 50 ký tự = 100%
#     spaces=$(( 50 - bars ))
    
#     progress=""
#     for ((j=0; j<bars; j++)); do progress="${progress}█"; done
#     for ((j=0; j<spaces; j++)); do progress="${progress}░"; done
    
#     # In dòng tiến trình (xóa dòng cũ bằng \r)
#     printf "\r[%3d%%] |%s| %02d giây còn lại | Vòng %02d/%02d" "$percent" "$progress" "$remaining" "$i" "$TOTAL"
    
#     sleep $INTERVAL
# done

# echo -e "\n\n✅ Hoàn thành 60 giây!"



cd /home/phuong/Documents/init-project-for-cloud
sudo docker-compose up -d --build

# sleep 7

# echo "Begin creating redis cluster"

# IP1=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis-node-1)
# IP2=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis-node-2)
# IP3=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis-node-3)
# IP4=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis-node-4)
# IP5=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis-node-5)
# IP6=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' redis-node-6)


# docker run -it --rm --network=app-network redis:6.2 \
#   redis-cli --cluster create \
#   $IP1:6379 $IP2:6379 $IP3:6379 \
#   $IP4:6379 $IP5:6379 $IP6:6379 \
#   --cluster-replicas 1

# for port in 7000 7001 7002 7003 7004 7005; do
#   echo "===== Node $port ====="
#   redis-cli -p $port cluster info
# done

# docker network inspect app-network

echo "Cluster created successfully"

