# Импорт BaseModel (базовый класс для Pydantic-моделей) и Field (для настройки полей)
from pydantic import BaseModel, Field
# Импорт Optional для полей, которые могут быть None
from typing import Optional


# Схема ВХОДЯЩЕГО запроса при создании/обновлении комментария (что клиент отправляет)
class CommentRequestTo(BaseModel):
    # ID твита, к которому относится комментарий (обязательное, >= 1)
    tweetId: int = Field(..., ge=1)
    # Текст комментария (обязательное, от 2 до 2048 символов)
    content: str = Field(..., min_length=2, max_length=2048, examples=["Comment content"])
    # Страна автора (необязательное, по умолчанию "Unknown", макс 64 символа)
    country: Optional[str] = Field(default="Unknown", max_length=64)


# Схема ИСХОДЯЩЕГО ответа при возврате комментария клиенту (что сервер отправляет)
class CommentResponseTo(BaseModel):
    # Уникальный идентификатор комментария (сгенерированный сервером)
    id: int
    # ID твита, к которому относится комментарий
    tweetId: int
    # Текст комментария
    content: str
    # Страна автора (по умолчанию "Unknown")
    country: str = "Unknown"
    # Статус модерации: PENDING, APPROVE, DECLINE
    state: str = "PENDING"
