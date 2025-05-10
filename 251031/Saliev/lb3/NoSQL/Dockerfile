# Используем официальный образ Python
FROM python:3.9-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем зависимости и исходный код
COPY requirements.txt .
COPY . .

# Устанавливаем зависимости
RUN pip install --no-cache-dir -r requirements.txt

# Запускаем FastAPI (если нужно)
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]

# ИЛИ запускаем тесты (если контейнер только для тестов)
# CMD ["pytest", "tests/", "-v"]