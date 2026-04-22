FROM python:3.13-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir -r requirements.txt

COPY . .

ENV PUBLISHER_PORT=8000

CMD ["sh", "-c", "uvicorn main:app --host 0.0.0.0 --http h11 --port ${PUBLISHER_PORT} --reload"]