FROM python:3.14.0-bookworm

ENV PYTHONUNBUFFERED=1
ENV PYTHONDONTWRITEBYTECODE=1

COPY --from=ghcr.io/astral-sh/uv:0.9.7 /uv /uvx /bin/

WORKDIR /app

COPY pyproject.toml uv.lock ./

RUN uv sync --locked #pip install -r requirements.txt

COPY src .

CMD ["uv", "run", "uvicorn", "main:app", "--host", "0.0.0.0", "--port", "24110"]