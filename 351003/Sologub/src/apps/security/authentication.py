"""DRF authentication classes for the v2.0 API."""
from rest_framework import authentication, exceptions

from apps.security.jwt_utils import JWTError, decode_token
from apps.writers.models import Writer


class _AuthedWriter:
    """Lightweight wrapper returned from authenticate().

    DRF only requires an object with ``is_authenticated``. We expose the
    underlying Writer instance plus the role read from the JWT claims so
    permission classes can inspect it cheaply.
    """

    def __init__(self, writer: Writer, role: str):
        self.writer = writer
        self.role = role

    @property
    def is_authenticated(self) -> bool:
        return True

    @property
    def id(self) -> int:
        return self.writer.id

    @property
    def login(self) -> str:
        return self.writer.login

    def __str__(self) -> str:
        return f"AuthedWriter(login={self.writer.login}, role={self.role})"


class JWTAuthentication(authentication.BaseAuthentication):
    keyword = "Bearer"

    def authenticate(self, request):
        header = request.META.get("HTTP_AUTHORIZATION", "")
        if not header:
            return None
        parts = header.split()
        if len(parts) != 2 or parts[0] != self.keyword:
            raise exceptions.AuthenticationFailed(
                "Authorization header must be 'Bearer <token>'"
            )
        token = parts[1]
        try:
            payload = decode_token(token)
        except JWTError as exc:
            raise exceptions.AuthenticationFailed(str(exc)) from exc

        login = payload.get("sub")
        try:
            writer = Writer.objects.get(login=login)
        except Writer.DoesNotExist as exc:
            raise exceptions.AuthenticationFailed(
                "Token references a writer that no longer exists"
            ) from exc

        role = payload.get("role") or writer.role
        return _AuthedWriter(writer, role), payload

    def authenticate_header(self, request):
        return self.keyword
