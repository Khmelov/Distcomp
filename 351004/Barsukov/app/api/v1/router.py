from fastapi import APIRouter

print("  --> Импортируем authors...")
from app.api.v1.endpoints import authors
print("  --> Импортируем issues...")
from app.api.v1.endpoints import issues
print("  --> Импортируем stickers...")
from app.api.v1.endpoints import stickers

api_router = APIRouter()

print("  --> Подключаем роутеры...")
api_router.include_router(authors.router,  prefix="/authors",  tags=["Authors"])
api_router.include_router(issues.router,   prefix="/issues",   tags=["Issues"])
api_router.include_router(stickers.router, prefix="/stickers", tags=["Stickers"])
print("  --> router.py полностью загружен")
