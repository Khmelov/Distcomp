print("--- ШАГ 1: Файл запущен ---") # Это должно напечататься первым

import uvicorn
from fastapi import FastAPI

print("--- ШАГ 2: Сейчас начнем импорт роутера ---")
try:
    from app.api.v1.router import api_router
    print("--- ШАГ 3: Роутер успешно импортирован ---")
except Exception as e:
    print(f"--- ОШИБКА ПРИ ИМПОРТЕ: {e} ---")

app = FastAPI()
app.include_router(api_router, prefix="/api/v1.0")

print(f"--- ШАГ 4: Переменная __name__ сейчас равна: {__name__} ---")

if __name__ == "__main__":
    print("--- ШАГ 5: Заходим в блок запуска uvicorn ---")
    uvicorn.run(app, host="127.0.0.1", port=24110)
else:
    print("--- ШАГ 5: Мы НЕ ЗАШЛИ в блок main, потому что файл импортирован как модуль ---")
