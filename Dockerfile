# ===============================
# Stage 1: Build (Maven)
# ===============================
FROM maven:3.9.14-eclipse-temurin-17 AS builder
WORKDIR /build

# Копируем pom.xml и скачиваем зависимости (кэшируем слой)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники и собираем jar
COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Runtime (JRE)
# ===============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Утилита curl для healthcheck
RUN apk add --no-cache curl

# Копируем собранный jar из builder
COPY --from=builder /build/target/*.jar app.jar

# Порты: HTTPS (8443) и management (8081)
EXPOSE 8443 8081

# Запуск приложения
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
