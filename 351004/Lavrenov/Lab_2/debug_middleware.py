# debug_middleware.py
from starlette.middleware.base import BaseHTTPMiddleware
from fastapi import Request
import json


class SimpleDebugMiddleware(BaseHTTPMiddleware):
    async def dispatch(self, request: Request, call_next):
        # Логируем входящий запрос
        print("\n" + "=" * 60)
        print(f">>> REQUEST: {request.method} {request.url}")
        print(f">>> HEADERS: {dict(request.headers)}")

        # Читаем тело и ВОССТАНАВЛИВАЕМ его
        body_bytes = await request.body()
        try:
            body_str = body_bytes.decode("utf-8")
            body_json = json.loads(body_str)
            print(f">>> BODY:\n{json.dumps(body_json, indent=2, ensure_ascii=False)}")
        except:
            print(f">>> BODY (raw): {body_bytes[:200]}...")

        # Восстановление потока
        async def receive():
            return {"type": "http.request", "body": body_bytes}

        request._receive = receive

        # Выполняем запрос
        response = await call_next(request)

        # Логируем ответ (без чтения тела, только статус и заголовки)
        print(f"<<< RESPONSE: {response.status_code}")
        print(f"<<< HEADERS: {dict(response.headers)}")
        print("=" * 60 + "\n")

        return response
