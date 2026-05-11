# Импорт datetime для типа полей created_at и modified_at в TweetResponseTo
from datetime import datetime

# Импорт Optional для полей, которые могут принимать значение None
from typing import Optional

# Импорт BaseModel (базовый класс Pydantic-моделей) и Field (для настройки валидации полей)
from pydantic import BaseModel, Field

# === DTO для Tweet (Data Transfer Object) ===

# Схема ВХОДЯЩЕГО запроса при создании/обновлении твита
class TweetRequestTo(BaseModel):
    # Заголовок твита: обязательный, от 2 до 64 символов
    title: str = Field(..., min_length=2, max_length=64, example="Мой первый твит")
    # Содержимое твита: обязательное, от 4 до 2048 символов
    content: str = Field(..., min_length=4, max_length=2048, example="Содержание твита")
    # ID автора твита: обязательный, >= 1
    writerId: int = Field(..., ge=1, example=1)

# Схема ИСХОДЯЩЕГО ответа при возврате твита клиенту
class TweetResponseTo(BaseModel):
    id: int                   # Уникальный ID твита
    title: str                # Заголовок
    content: str              # Содержимое
    created_at: datetime      # Дата создания
    modified_at: datetime     # Дата последнего изменения
    writerId: int             # ID автора

    model_config = {"from_attributes": True}


# === DTO для Writer ===

# Схема ВХОДЯЩЕГО запроса при создании/обновлении автора
class WriterRequestTo(BaseModel):
    # Логин: обязательный, от 2 до 64 символов
    login: str = Field(..., min_length=2, max_length=64, example="email")
    # Пароль: обязательный, от 8 до 128 символов
    password: str = Field(..., min_length=8, max_length=128, example="1234")
    # Имя: обязательное, от 2 до 64 символов
    firstname: str = Field(..., min_length=2, max_length=64, example="Egor")
    # Фамилия: обязательная, от 2 до 64 символов
    lastname: str = Field(..., min_length=2, max_length=64, example="Antipov")

# Схема ИСХОДЯЩЕГО ответа при возврате автора клиенту
class WriterResponseTo(BaseModel):
    id: int          # Уникальный ID автора
    login: str       # Логин
    password: str    # Пароль
    firstname: str   # Имя
    lastname: str    # Фамилия

    model_config = {"from_attributes": True}


# === DTO для Security (v2.0) ===

class RegisterRequestTo(BaseModel):
    login: str = Field(..., min_length=2, max_length=64)
    password: str = Field(..., min_length=8, max_length=128)
    firstname: str = Field(..., min_length=2, max_length=64, alias="firstName")
    lastname: str = Field(..., min_length=2, max_length=64, alias="lastName")
    role: str = Field(default="CUSTOMER")

    model_config = {"populate_by_name": True}

class LoginRequestTo(BaseModel):
    login: str
    password: str

class LoginResponseTo(BaseModel):
    access_token: str
    token_type: str = "bearer"

class ErrorResponseTo(BaseModel):
    errorMessage: str
    errorCode: int


# === DTO для Comment ===
# Эти DTO используются в publisher как модели для проксирования запросов в discussion

# Схема ВХОДЯЩЕГО запроса при создании/обновлении комментария
class CommentRequestTo(BaseModel):
    # ID твита, к которому относится комментарий: обязательный, >= 1
    tweetId: int = Field(..., ge=1)
    # Текст комментария: обязательный, от 2 до 2048 символов
    content: str = Field(..., min_length=2, max_length=2048, examples=["Content"])
    # Страна автора: необязательная, по умолчанию "Unknown"
    country: Optional[str] = Field(default="Unknown", max_length=64)

# Схема ИСХОДЯЩЕГО ответа при возврате комментария клиенту
class CommentResponseTo(BaseModel):
    id: int                      # Уникальный ID комментария
    tweetId: int                 # ID твита
    content: str                 # Текст комментария
    country: str = "Unknown"     # Страна автора
    state: str = "PENDING"       # Статус модерации: PENDING, APPROVE, DECLINE

# === DTO для Sticker ===

# Схема ВХОДЯЩЕГО запроса при создании/обновлении стикера
class StickerRequestTo(BaseModel):
    # Название стикера: обязательное, от 2 до 32 символов
    name: str = Field(..., min_length=2, max_length=32, example="Sticker")

# Схема ИСХОДЯЩЕГО ответа при возврате стикера клиенту
class StickerResponseTo(BaseModel):
    id: int    # Уникальный ID стикера
    name: str  # Название стикера

    model_config = {"from_attributes": True}
