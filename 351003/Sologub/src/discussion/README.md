# Модуль Discussion (микросервис Note)

Хранит сущность **Note** в Cassandra. Внешнее API приложения не меняется: клиенты обращаются к publisher (`/api/v1.0/notes`), который проксирует запросы сюда.

## Схема Cassandra (ключи без data skew)

- **Ключ партиционирования**: `story_id` — все заметки одной истории в одной партиции (эффективные запросы по story, равномерное распределение).
- **Ключ кластеризации**: `id` — уникальность и сортировка внутри партиции.
- **Поле** `country` — обычное поле (не ключ), чтобы избежать перекоса по странам.

Таблицы в keyspace `distcomp` с префиксом `tbl_`:

- `tbl_note`: `PRIMARY KEY (story_id, id)`, поля `content`, `country`.
- `tbl_note_by_id`: `PRIMARY KEY (id)`, поле `story_id` — для получения заметки по `id` без знания `story_id`.

## Подключение

- Адрес: `localhost` (в Docker: сервис `cassandra`).
- Порт: `9042`.
- Keyspace: `distcomp`.
- User/password не используются.

## Запуск

Из корня репозитория:

```bash
pipenv run python src/discussion/manage.py runserver 0.0.0.0:24130
```

В Docker контейнер `discussion` стартует с `working_dir: /app/src/discussion` и командой `python manage.py runserver 0.0.0.0:24130`.

## API

Префикс: `/api/v1.0/`.

- `GET /notes?storyId=<id>` — список заметок по истории.
- `POST /notes` — тело: `{"storyId": int, "content": str, "country": str?}`.
- `GET /notes/<id>`, `PUT /notes/<id>`, `DELETE /notes/<id>`.

Доступ к данным через интерфейс **Repository** (`repository/interface.py`, реализация в `repository/cassandra_repository.py`).
