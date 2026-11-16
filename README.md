# Bank

Сервис рекомендаций банковских продуктов. Подбирает клиентам подходящие продукты по истории их транзакций. Рекомендации выдаются через REST API и Telegram-бота. Правила подбора бывают статические (в коде) и динамические (создаются через API и хранятся в базе данных).

## Технологии

Java 17, Spring Boot 3.4.4, Maven, H2, PostgreSQL, Liquibase, Spring Data JPA, JdbcTemplate, Caffeine, Telegram Bot API, Swagger

## Возможности

- Рекомендации по id пользователя: `GET /recommendation/{user_id}`
- Управление динамическими правилами: `POST /rule`, `GET /rule`, `DELETE /rule/{product_id}`
- Статистика срабатываний правил: `GET /rule/stats`
- Telegram-бот: команда `/recommend username`
- Служебные запросы: `POST /management/clear-caches`, `GET /management/info`

## Документация

- [Требования](https://github.com/xlebusek-kate/Bank/wiki/Требования)
- [Архитектура](https://github.com/xlebusek-kate/Bank/wiki/Архитектура)
- [REST API](https://github.com/xlebusek-kate/Bank/wiki/REST-API)
- [Развёртывание](https://github.com/xlebusek-kate/Bank/wiki/Развёртывание)

## Быстрый запуск

1. Создай пустую базу PostgreSQL и задай переменные среды (см. страницу «Развёртывание»).
2. Собери проект: `./mvnw package`
3. Запусти: `java -jar target/Bank-0.0.1-SNAPSHOT.jar`

## Автор

Денис Черняков
