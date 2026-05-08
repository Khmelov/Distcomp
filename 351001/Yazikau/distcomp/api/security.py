import jwt
import datetime
from rest_framework import authentication, exceptions, permissions
from .models import Author

# ВЕРНУЛИ ТВОЙ ПРАВИЛЬНЫЙ КЛЮЧ
SECRET_KEY = 'my_super_secret_jwt_key_for_distcomp'


def generate_jwt_token(author):
    payload = {
        'id': author.id,
        'role': author.role,
        'exp': datetime.datetime.utcnow() + datetime.timedelta(hours=1),
        'iat': datetime.datetime.utcnow()
    }
    token = jwt.encode(payload, SECRET_KEY, algorithm='HS256')

    # ИСПРАВЛЕНИЕ БАГА: Если библиотека вернула байты, превращаем их в чистую строку
    if isinstance(token, bytes):
        return token.decode('utf-8')
    return token


class CustomJWTAuthentication(authentication.BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get('Authorization')

        if not auth_header or not auth_header.startswith('Bearer '):
            return None

        try:
            # Получаем сырой токен
            raw_token = auth_header.split(' ')[1]

            # ЖЕСТКАЯ ОЧИСТКА: удаляем возможные артефакты типа b'...' или кавычек
            clean_token = raw_token.strip()
            if clean_token.startswith("b'") or clean_token.startswith('b"'):
                clean_token = clean_token[2:-1]  # Отрезаем b' спереди и ' сзади
            clean_token = clean_token.strip("'").strip('"')

            # Декодируем
            payload = jwt.decode(clean_token, SECRET_KEY, algorithms=['HS256'])

            author_id = payload.get('id')
            if not author_id:
                author_id = payload.get('user_id')

            author = Author.objects.get(id=author_id)
            return (author, clean_token)

        except jwt.ExpiredSignatureError:
            raise exceptions.AuthenticationFailed('Token expired')
        except Author.DoesNotExist:
            raise exceptions.AuthenticationFailed('Invalid token')
        except Exception as e:
            # На случай, если вылезет что-то еще
            print(f"\n[DEBUG] ОШИБКА: {repr(e)} | СЫРОЙ ТОКЕН БЫЛ: {auth_header}\n")
            raise exceptions.AuthenticationFailed('Invalid token')

    def authenticate_header(self, request):
        return 'Bearer'


class IsAuthenticatedV2(permissions.BasePermission):
    def has_permission(self, request, view):
        return bool(request.user and isinstance(request.user, Author))


class IsAdminRole(permissions.BasePermission):
    def has_permission(self, request, view):
        return bool(request.user and isinstance(request.user, Author) and request.user.role == 'admin')