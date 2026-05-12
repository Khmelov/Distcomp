FROM python:3.11-slim

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 24110

CMD ["sh", "-lc", "alembic upgrade head && python -m hypercorn main:app --bind 0.0.0.0:24110"]