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

# Ensure cassandra-driver is installed cleanly without scylla-driver (which
# conflicts in the same cassandra/ namespace and causes runtime import errors
# like "cannot import name OperationType"). kafka-python is pinned to a version
# that works on Python 3.12.
RUN pip uninstall -y scylla-driver django-cassandra-engine kafka-python || true \
 && pip install --no-cache-dir --force-reinstall "cassandra-driver==3.29.2" \
 && pip install --no-cache-dir "kafka-python-ng==2.2.3" "redis==5.0.8" \
 && pip install --no-cache-dir "PyJWT==2.9.0" "bcrypt==4.2.0"

EXPOSE 24110 24130

COPY src/ ./src

WORKDIR /app/src

CMD ["python3", "manage.py", "runserver", "0.0.0.0:24110"]