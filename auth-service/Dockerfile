# ============================= Stage 1: Download dependencies ================================
FROM maven:3.9.6-eclipse-temurin-17 AS deps
WORKDIR /app
COPY pom.xml .
# RUN mvn dependency:go-offline -B
RUN mvn dependency:resolve dependency:resolve-plugins -B

# ============================= Stage 2: Build project ================================
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY --from=deps /root/.m2 /root/.m2
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

# ================================= Stage 3: Runtime =================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8005
ENTRYPOINT ["java","-jar","app.jar"]
