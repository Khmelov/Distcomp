from fastapi import APIRouter

print("  --> Импортируем authors...")
from api.v1.endpoints import authors
print("  --> Импортируем issues...")
from api.v1.endpoints import issues
print("  --> Импортируем stickers...")
from api.v1.endpoints import stickers, notes

api_router = APIRouter()

print("  --> Подключаем роутеры...")
api_router.include_router(authors.router,  prefix="/authors",  tags=["Authors"])
api_router.include_router(issues.router,   prefix="/issues",   tags=["Issues"])
api_router.include_router(stickers.router, prefix="/stickers", tags=["Stickers"])
api_router.include_router(notes.router,    prefix="/notes",    tags=["Notes"])
print("  --> router.py полностью загружен")