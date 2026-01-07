# Task 320: JPA REST API

## Запуск проекта

python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
uvicorn app.main:app --host 0.0.0.0 --port 24120 --reload

## Тестирование

python -m pytest -v

## Документация

Swagger UI: http://localhost:24120/docs
ReDoc: http://localhost:24120/redoc