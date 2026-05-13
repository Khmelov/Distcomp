import jwt
import datetime
from django.conf import settings


def generate_token(editor):
    now = datetime.datetime.now(datetime.timezone.utc)
    payload = {
        'sub': editor.login,
        'iat': now,
        'exp': now + settings.JWT_ACCESS_TOKEN_LIFETIME,
        'role': editor.role,
        'editor_id': editor.id,
    }
    token = jwt.encode(payload, settings.JWT_SECRET_KEY, algorithm=settings.JWT_ALGORITHM)
    return token


def decode_token(token):
    try:
        payload = jwt.decode(token, settings.JWT_SECRET_KEY, algorithms=[settings.JWT_ALGORITHM])
        return payload
    except jwt.ExpiredSignatureError:
        return None
    except jwt.InvalidTokenError:
        return None
