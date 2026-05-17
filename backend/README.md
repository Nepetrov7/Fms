# FMS - Система управления дорожной картой для мигрантов

## Описание

Приложение для ФМС, которое помогает мигрантам получить персональную дорожную карту действий на основе их данных.

## Технологии

### Backend

-   Spring Boot 4.0
-   Spring Security с JWT
-   Spring Data JPA
-   H2 Database (in-memory)
-   Lombok
-   Java 21

### Frontend

-   React 18
-   TypeScript
-   Vite
-   React Router
-   Axios
-   SCSS Modules

## Запуск приложения

### Требования

**Backend:**

-   Java 21 или выше
-   Maven 3.6+

**Frontend:**

-   Node.js 18+ и npm

### Команды для запуска

**Backend:**

```bash
# Сборка проекта
mvn clean install

# Запуск приложения
mvn spring-boot:run
```

Backend будет доступен по адресу: `http://localhost:8080`

**Frontend:**

```bash
# Перейти в папку frontend
cd frontend

# Установить зависимости
npm install

# Запуск в режиме разработки
npm run dev
```

Frontend будет доступен по адресу: `http://localhost:3000`

**Авторизация:** сначала **регистрация** (`/register`) или **вход** (`/login`), затем заполнение профиля и дорожная карта. API защищено JWT (`Authorization: Bearer ...`).

**Админ-панель** (дерево задач, настройки приложения, справочники): `http://localhost:3000/admin` — также ссылка внизу страницы профиля (пока без отдельной роли администратора).

**Сборка фронтенда для продакшена:**

```bash
cd frontend
npm run build
```

Собранные файлы будут автоматически скопированы в `src/main/resources/static` и будут раздаваться через Spring Boot.

## Структура проекта

```
fms/
├── frontend/            # React фронтенд приложение
│   ├── src/
│   │   ├── components/  # React компоненты
│   │   ├── context/     # React контексты
│   │   ├── services/    # API сервисы
│   │   ├── types/       # TypeScript типы
│   │   └── utils/       # Утилиты
│   └── package.json
├── src/main/java/com/example/fms/
│   ├── config/          # Конфигурация (Security, DataInitializer)
│   ├── controller/      # REST контроллеры
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA сущности
│   ├── exception/       # Обработка исключений
│   ├── repository/      # JPA репозитории
│   ├── security/        # Security компоненты
│   ├── service/         # Бизнес-логика
│   └── util/            # Утилиты (JWT)
└── pom.xml
```

## Основные функции

1. **Регистрация и авторизация** - JWT-based аутентификация
2. **Управление профилем** - заполнение данных пользователя
3. **Дорожная карта** - автоматическое формирование списка действий

## API Документация

Подробная документация API находится в файле [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

## База данных

Используется H2 in-memory база данных. Для доступа к консоли:

-   URL: `http://localhost:8080/h2-console`
-   JDBC URL: `jdbc:h2:mem:fmsdb`
-   Username: `sa`
-   Password: (пусто)

## Инициализация данных

При первом запуске автоматически создаются пункты дорожной карты:

-   Сертификат владения русским языком (3 пункта)
-   Патент на работу (4 пункта)
-   Оплата госпошлины за патент (3 пункта)

## Порядок приоритетов дорожной карты

1. **Сертификат владения русским языком** (самый важный)
2. **Патент на работу**
3. **Оплата госпошлины за патент**

## Дополнительная документация

-   [API Документация](API_DOCUMENTATION.md) - подробное описание REST API
-   [Frontend README](frontend/README.md) - документация по фронтенд приложению
