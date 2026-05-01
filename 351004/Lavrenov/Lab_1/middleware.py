# middleware.py
import time
import json
from fastapi import Request
from starlette.middleware.base import BaseHTTPMiddleware
from logger import app_logger


class LoggingMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        start_time = time.time()

        # Логирование входящего запроса
        method = request.method
        url = str(request.url)
        client_host = request.client.host if request.client else "unknown"

        # Попытка прочитать тело запроса (с осторожностью, т.к. после чтения тело становится недоступным)
        # Для простоты логируем только информацию о запросе без тела
        app_logger.info(f"Request: {method} {url} from {client_host}")

        # Выполнение запроса
        response = await call_next(request)

        # Время обработки
        process_time = time.time() - start_time

        # Логирование ответа
        status_code = response.status_code
        app_logger.info(
            f"Response: {method} {url} -> {status_code} ({process_time:.3f}s)"
        )

        # Добавляем заголовок с временем обработки (опционально)
        response.headers["X-Process-Time"] = str(process_time)

        return response
