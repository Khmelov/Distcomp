FROM python:3.12-slim AS builder

WORKDIR /app

RUN --mount=type=cache,target=/root/.cache/pip \
    pip install pipenv

COPY Pipfile .
COPY Pipfile.lock .

RUN --mount=type=cache,target=/root/.cache/pip \
    pipenv requirements > requirements.txt

FROM python:3.12-slim

WORKDIR /app

COPY --from=builder /app/requirements.txt .

RUN --mount=type=cache,target=/root/.cache/pip \
    pip install --no-cache-dir -r requirements.txt

# Модуль discussion использует cassandra-driver напрямую (не только через django-cassandra-engine).
RUN pip install --no-cache-dir cassandra-driver

EXPOSE 24110 24130

COPY src/ ./src

WORKDIR /app/src

# Команда по умолчанию для publisher; discussion переопределяет в compose.
CMD ["python3", "manage.py", "runserver", "0.0.0.0:24110"]