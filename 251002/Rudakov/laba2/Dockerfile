# FROM python:3.12-alpine

# WORKDIR /app

# COPY requirements.txt .

# RUN pip install --no-cache-dir -r requirements.txt

# COPY . . 

# CMD ["sh", "-c", "while true; do python -m hypercorn main:app --reload --bind 0.0.0.0:24110; echo 'Hypercorn упал, перезапуск через 1 секунду...'; sleep 1; done"]

# # CMD ["python", "-m", "hypercorn", "main:app", "--reload", "--bind", "0.0.0.0:24110"]

FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

CMD ["sh", "-c", "while true; do python -m hypercorn main:app --reload --bind 0.0.0.0:24110; echo 'Hypercorn упал, перезапуск через 1 секунду...'; sleep 1; done"]