import datetime
from jose import jwt
import bcrypt # Используем напрямую

SECRET_KEY = "MY_SUPER_SECRET_KEY_DONT_SHARE"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 60

def verify_password(plain_password: str, hashed_password: str):
    # Проверка пароля: сравниваем чистый пароль с хешем из БД
    password_bytes = plain_password.encode('utf-8')
    hashed_bytes = hashed_password.encode('utf-8')
    return bcrypt.checkpw(password_bytes, hashed_bytes)

def get_password_hash(password: str):
    # Хеширование: генерируем соль и хешируем
    pwd_bytes = password.encode('utf-8')
    salt = bcrypt.gensalt()
    hashed = bcrypt.hashpw(pwd_bytes, salt)
    return hashed.decode('utf-8') # Возвращаем как строку для записи в БД

def create_access_token(data: dict):
    to_encode = data.copy()
    now = datetime.datetime.utcnow()
    expire = now + datetime.timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({
        "exp": expire,
        "iat": now,
        "sub": str(data.get("sub"))
    })
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)