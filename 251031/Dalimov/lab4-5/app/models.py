from pydantic import BaseModel

# Модель для запроса создания пользователя
class UserRequestTo(BaseModel):
    name: str
    age: int

# Модель для ответа с данными пользователя
class UserResponseTo(UserRequestTo):
    id: str
