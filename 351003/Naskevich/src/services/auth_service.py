from fastapi import HTTPException

from src.cache import keys as cache_keys
from src.cache.redis_cache import RedisCache
from src.dto.auth import LoginRequest, LoginResponse
from src.dto.editor import EditorOut, EditorRegisterV2
from src.exceptions import EntityAlreadyExistsException
from src.models.editor import Editor
from src.repositories.editor import AbstractEditorRepository
from src.database.uow import UnitOfWork
from src.security.jwt_tokens import create_access_token
from src.security.passwords import hash_password, verify_password


class AuthService:
    def __init__(
        self,
        repository: AbstractEditorRepository,
        uow: UnitOfWork,
        cache: RedisCache,
    ) -> None:
        self._repo = repository
        self._uow = uow
        self._cache = cache

    async def _invalidate_editor_cache(self, editor_id: int | None = None) -> None:
        keys = [cache_keys.editors_all()]
        if editor_id is not None:
            keys.append(cache_keys.editor(editor_id))
        await self._cache.delete(*keys)

    async def register(self, data: EditorRegisterV2) -> EditorOut:
        existing = await self._repo.get_by_login(data.login)
        if existing is not None:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        editor = Editor(
            login=data.login,
            password=hash_password(data.password),
            firstname=data.firstname,
            lastname=data.lastname,
            role=data.role,
        )
        created = await self._repo.create(editor)
        await self._uow.commit()
        await self._invalidate_editor_cache()
        return EditorOut.model_validate(created)

    async def login(self, data: LoginRequest) -> LoginResponse:
        editor = await self._repo.get_by_login(data.login)
        if editor is None or not verify_password(data.password, editor.password):
            raise HTTPException(status_code=401, detail="Invalid login or password")
        token = create_access_token(sub=editor.login, role=editor.role.value)
        return LoginResponse(access_token=token)
