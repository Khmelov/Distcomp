import jwt
import datetime
from functools import wraps
from flask import request, jsonify
from app.config import Config


class JWTHandler:
    """Обработчик JWT токенов"""

    @staticmethod
    def generate_token(login: str, role: str) -> str:
        """Генерация JWT токена"""
        now = datetime.datetime.utcnow()
        payload = {
            'sub': login,
            'iat': now,
            'exp': now + datetime.timedelta(hours=Config.JWT_EXPIRATION_HOURS),
            'role': role
        }
        return jwt.encode(payload, Config.JWT_SECRET_KEY, algorithm='HS256')

    @staticmethod
    def decode_token(token: str) -> dict:
        """Декодирование JWT токена"""
        try:
            return jwt.decode(token, Config.JWT_SECRET_KEY, algorithms=['HS256'])
        except jwt.ExpiredSignatureError:
            return None
        except jwt.InvalidTokenError:
            return None

    @staticmethod
    def get_token_from_header() -> str:
        """Извлечение токена из заголовка Authorization"""
        auth_header = request.headers.get('Authorization', '')
        if auth_header.startswith('Bearer '):
            return auth_header[7:]
        return None


def require_auth(f):
    """Декоратор для проверки аутентификации"""

    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = JWTHandler.get_token_from_header()
        if not token:
            return jsonify({
                "errorMessage": "Authentication required. Provide Bearer token.",
                "errorCode": "40101"
            }), 401

        payload = JWTHandler.decode_token(token)
        if not payload:
            return jsonify({
                "errorMessage": "Invalid or expired token.",
                "errorCode": "40102"
            }), 401

        request.current_user = {
            'login': payload.get('sub'),
            'role': payload.get('role')
        }
        return f(*args, **kwargs)

    return decorated_function


def require_role(roles: list):
    """Декоратор для проверки роли"""

    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            if not hasattr(request, 'current_user'):
                return jsonify({
                    "errorMessage": "Authentication required.",
                    "errorCode": "40101"
                }), 401

            user_role = request.current_user.get('role')
            if user_role not in roles:
                return jsonify({
                    "errorMessage": f"Access denied. Required role: {roles}",
                    "errorCode": "40301"
                }), 403

            return f(*args, **kwargs)

        return decorated_function

    return decorator