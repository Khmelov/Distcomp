FROM python:3.14.3-slim-trixie

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1
ENV PYTHONPATH /app

COPY pyproject.toml .
RUN pip install --no-cache-dir .

COPY src/ ./src/

CMD ["uvicorn", "src.main:app", "--host", "0.0.0.0", "--port", "24110", "--reload"]