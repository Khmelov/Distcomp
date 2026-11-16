# Publisher и discussion

Учебный сервер на Spring Boot. Пользователи, истории и маркеры лежат в PostgreSQL. Сообщения вынесены в модуль `discussion` и хранятся в Cassandra. Внешний контракт прежний.

| Модуль | Адрес | Хранилище |
|---|---|---|
| publisher | `http://localhost:24110/api/v1.0` | PostgreSQL, схема `distcomp` |
| discussion | `http://localhost:24130/messages` | Cassandra, keyspace `distcomp` |

Ход этого этапа записан в `3.Cassandra README.md`.

## Запуск

Нужен JDK 21. PostgreSQL: `localhost:5432`, база `distcomp`, пользователь и пароль `postgres`. Cassandra: `localhost:9042`, без пользователя и пароля.

```bash
export JAVA_HOME=/home/levzh/.local/jdks/jdk-21.0.13+9
export PATH="$JAVA_HOME/bin:$PATH"
mvn -pl discussion spring-boot:run
mvn -pl publisher spring-boot:run
```

Команды запускаются из корня `Zaluzhny`, каждая в своём терминале. `mvn test` гоняет оба модуля. Тест Cassandra пропускается, если Docker недоступен. На прогоне 16.11.2026 publisher прошёл 31 тест, discussion — 25, один тест контейнера Cassandra пропущен.
