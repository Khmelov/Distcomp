from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed
from .jwt_utils import decode_token
from .models import Editor


class EditorUser:
    """Wrapper to make Editor act like an authenticated user for DRF."""

    def __init__(self, editor):
        self.editor = editor
        self.id = editor.id
        self.login = editor.login
        self.role = editor.role
        self.is_authenticated = True


class JWTAuthentication(BaseAuthentication):
    def authenticate(self, request):
        auth_header = request.headers.get('Authorization')
        if not auth_header:
            return None

        parts = auth_header.split()
        if len(parts) != 2 or parts[0] != 'Bearer':
            return None

        token = parts[1]
        payload = decode_token(token)
        if payload is None:
            raise AuthenticationFailed('Invalid or expired token')

        login = payload.get('sub')
        try:
            editor = Editor.objects.get(login=login)
        except Editor.DoesNotExist:
            raise AuthenticationFailed('User not found')

        return (EditorUser(editor), token)

    def authenticate_header(self, request):
        return 'Bearer'
