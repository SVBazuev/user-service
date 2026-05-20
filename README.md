# User Service

![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk&color=orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-green?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql)
![Apache Kafka](https://img.shields.io/badge/Kafka-7.4-black?logo=apachekafka)
![Maven](https://img.shields.io/badge/Maven-3.9.14-blue?logo=apachemaven)
![Liquibase](https://img.shields.io/badge/Liquibase-4.27-red?logo=liquibase)
![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)

# Описание проекта

REST‑сервис для управления пользователями с поддержкой операций **Create**, **Read**, **Update**, **Delete** (CRUD).
Построен на **Spring Boot 3**, использует **Spring Data JPA**, **PostgreSQL** и **Kafka** для асинхронной отправки уведомлений в отдельный микросервис.

**Основные возможности:**
- Регистрация, просмотр, обновление и удаление пользователей.
- Ролевая модель (ADMIN / USER) на основе Spring Security (HTTP Basic).
- Хэширование паролей BCrypt.
- Кэширование запросов к БД (Caffeine).
- Rate limiting (20 запросов / 60 секунд на клиента).
- HTTPS с самоподписанным сертификатом и отдельный management порт (Actuator).
- Асинхронная отправка уведомлений в Kafka (с fallback на REST при недоступности Kafka).
- Миграции схемы БД через Liquibase.
- Полное покрытие модульными и интеграционными тестами (Testcontainers, JaCoCo).

# Технологии

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Data JPA** (Hibernate)
- **Spring Security** (Basic Auth, BCrypt)
- **Spring Kafka** (Producer)
- **Spring Cache** + **Caffeine**
- **Spring Boot Actuator**
- **PostgreSQL 15**
- **Liquibase** (миграции)
- **Maven**
- **Docker** + **Docker Compose**
- **Testcontainers** + **JUnit 5** + **Mockito** + **JaCoCo**

# API‑эндпоинты

Все эндпоинты (кроме actuator) требуют Basic аутентификации.
`/api/users/**` доступен для ролей **USER** и **ADMIN**, но **POST** и **DELETE** – только для **ADMIN**.

| Метод   | Путь                        | Описание                         |
|---------|-----------------------------|----------------------------------|
| POST    | `/api/users`                | Создание пользователя (ADMIN)    |
| GET     | `/api/users`                | Список всех пользователей        |
| GET     | `/api/users/{id}`           | Получение пользователя по ID     |
| PUT     | `/api/users/{id}`           | Обновление пользователя (ADMIN)  |
| DELETE  | `/api/users/{id}`           | Удаление пользователя (ADMIN)    |
| GET     | `/actuator/health`          | Healthcheck (без авторизации)    |
| GET     | `/actuator/info`            | Информация о приложении          |
| GET     | `/actuator/metrics`         | Метрики (без авторизации)        |

# Требования к окружению

- **Docker Desktop** (для запуска PostgreSQL и Kafka)
- **Java 17** (установлена и настроена)
- **Maven 3.9+** (либо встроенный в IDEA)

# Быстрый старт (локальная разработка)

## 1. Клонирование репозитория

```bash
git clone https://github.com/ваш-репозиторий/user-service.git
cd user-service
```

## 2. Запуск инфраструктуры (PostgreSQL + Zookeeper + Kafka)

```bash
docker-compose up -d
```

> **Примечание:** сервис `app` в `docker-compose.yml` закомментирован – приложение вы будете запускать из IDE или через Maven.

Проверьте, что все контейнеры здоровы:

```bash
docker ps
```

## 3. Настройка переменных окружения (опционально)

По умолчанию используются значения из `application.properties`.
При необходимости переопределите их через переменные окружения или `application-{profile}.properties`.

## 4. Запуск приложения

Через Maven:

```bash
mvn spring-boot:run
```

Или запустите класс `UserServiceApplication` в вашей IDE.

После запуска приложение будет доступно по адресу **https://localhost:8443**.
Management‑эндпоинты – **http://localhost:8081/actuator/health**.

## 5. Аутентификация

При первом запуске создаются два пользователя:

| Логин            | Пароль     | Роли          |
|------------------|------------|---------------|
| `admin@demo.ya`  | `admin123` | ADMIN, USER   |
| `user@demo.ya`   | `user123`  | USER          |

Пример запроса через `curl`:

```bash
curl -k -u admin@demo.ya:admin123 https://localhost:8443/api/users
```

## 6. Остановка контейнеров

```bash
docker-compose down
```

Чтобы полностью удалить тома (сбросить БД):

```bash
docker-compose down -v
```

# Архитектура проекта

```text
src/main/java/edu/example
├── api/                  # REST-контроллеры и глобальный обработчик ошибок
├── config/               # Конфигурации (Security, Cache, RateLimit, Async, JPA Auditing)
├── core/
│   ├── dto/              # DTO, мапперы (MapStruct), группы валидации
│   ├── entity/           # JPA‑сущности (User, UserRole, RoleListConverter)
│   ├── event/            # События (UserCreatedEvent, UserDeletedEvent)
│   ├── exception/        # Кастомные исключения
│   ├── kafka/            # Топики
│   ├── notification/     # Отправка уведомлений в Kafka и REST fallback
│   └── service/          # Бизнес-логика
├── repository/           # Spring Data JPA репозитории
└── resources/
    ├── db/changelog/     # Миграции Liquibase
    ├── keystore.p12      # Самоподписанный сертификат для HTTPS
    └── application.properties
```

# Тестирование

## Запуск тестов

```bash
mvn clean verify
```

**Типы тестов:**
- **Модульные** (`**/*Test.java`) – сервисы, мапперы, исключения.
- **Интеграционные** (`**/*IT.java`) – репозитории с Testcontainers, Kafka, полные сценарии.
- **E2E** – `UserServiceApplicationIT` проверяет полный CRUD‑цикл через REST.

**JaCoCo** настроен на порог покрытия инструкций **70%** (DTO, конфигурации, сущности исключены).

# Особенности реализации

- **Безопасность:** Basic Auth, роли ADMIN/USER, BCrypt, HTTPS.
- **Кэширование:** Caffeine на методах `findById` и `findByEmail`, инвалидация при создании/обновлении/удалении.
- **Rate limiting:** 20 запросов / 60 секунд на клиента (идентификация по IP или Basic Auth).
- **Асинхронные уведомления:** после фиксации транзакции события публикуются в Kafka, при недоступности – fallback через REST в `notification-service`.
- **Actuator:** health, info, metrics на отдельном порту (без SSL).
- **Миграции:** Liquibase с одним changeset (создание таблицы `users`, добавление JSONB‑колонки `roles`).
- **Тесты:** Testcontainers для PostgreSQL и Kafka, EmbeddedKafka для тестов consumer'а.

# CI (GitHub Actions)

При каждом push в ветки `main`/`develop` запускается:
- Установка JDK 17 и кеширование Maven.
- `mvn clean verify` (тесты + проверка покрытия JaCoCo).
- Сборка Docker-образа (опционально).

# Автор

[Станислав Базуев / SVBazuev](https://github.com/SVBazuev)
