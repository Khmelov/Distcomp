from datetime import datetime, UTC, timedelta

import bcrypt
import jwt
from sqlalchemy.ext.asyncio import AsyncSession

from src.core import settings
from src.core.errors import UnauthorizedError
from src.domain.repositories import AuthorRepository
from src.schemas import LoginResponse, LoginRequest, GenerateTokenRequest
from src.schemas.auth import ValidTokenResponse


class AuthService:
    def __init__(self, session: AsyncSession):
        self.session = session
        self.author_repo = AuthorRepository(session)

    async def validate_token(self, token: str) -> ValidTokenResponse | None:
        try:
            payload = jwt.decode(
                token,
                settings.auth.secret_key,
                algorithms=[settings.auth.algorithm]
            )
        except jwt.InvalidTokenError:
            return None

        return ValidTokenResponse(
            login=payload.get("sub"),
            role=payload.get("role")
        )

    async def login(self, request: LoginRequest) -> LoginResponse:
        user = await self.author_repo.get_by_login(request.login)

        if not user or not self._verify_password(request.password, user.password):
            raise UnauthorizedError("User not found")

        token = self._generate_token(
            GenerateTokenRequest(
                login=request.login,
                role=user.role
            )
        )
        return LoginResponse(access_token=token)


    @staticmethod
    def _generate_token(request: GenerateTokenRequest) -> str:
        now_time = datetime.now(UTC)
        expire = now_time + timedelta(seconds=settings.auth.token_lifetime)
        to_encode = {
            "sub": request.login,
            "iat": now_time,
            "exp": expire,
            "role": request.role,
        }

        return jwt.encode(
            to_encode,
            settings.auth.secret_key,
            algorithm=settings.auth.algorithm
        )

    @staticmethod
    def get_hashed_password(password: str) -> str:
        password_bytes = password.encode('utf-8')
        salt = bcrypt.gensalt()
        hashed_bytes = bcrypt.hashpw(password_bytes, salt)

        return hashed_bytes.decode('utf-8')

    @staticmethod
    def _verify_password(plain_password: str, hashed_password: str) -> bool:
        password_bytes = plain_password.encode('utf-8')
        hash_bytes = hashed_password.encode('utf-8')

        return bcrypt.checkpw(password_bytes, hash_bytes)