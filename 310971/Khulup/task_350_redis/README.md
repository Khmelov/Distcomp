# Task 350: Redis

## Запуск проекта

```bash
docker compose up -d
```

```bash
cd publisher
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
python main.py
```

```bash
cd discussion
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
python main.py
```

## Тестирование

```bash
cd discussion
.venv\Scripts\activate
python -m pytest -v
```

```bash
cd publisher
.venv\Scripts\activate
python -m pytest -v
```

## Документация

- Publisher Swagger UI: http://localhost:24110/docs
- Publisher ReDoc: http://localhost:24110/redoc
- Discussion Swagger UI: http://localhost:24130/docs
- Discussion ReDoc: http://localhost:24130/redoc