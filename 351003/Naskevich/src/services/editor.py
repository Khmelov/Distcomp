from src.cache import keys as cache_keys
from src.cache.redis_cache import RedisCache
from src.database.uow import UnitOfWork
from src.dto.editor import EditorOut, EditorRequestTo
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException
from src.models.editor import Editor
from src.models.user_role import UserRole
from src.repositories.editor import AbstractEditorRepository
from src.security.passwords import hash_password


class EditorService:

    def __init__(
        self,
        repository: AbstractEditorRepository,
        uow: UnitOfWork,
        cache: RedisCache,
        cache_ttl_seconds: int,
    ) -> None:
        self._repo = repository
        self._uow = uow
        self._cache = cache
        self._ttl = cache_ttl_seconds

    async def _invalidate_editors(self, editor_id: int | None = None) -> None:
        keys = [cache_keys.editors_all()]
        if editor_id is not None:
            keys.append(cache_keys.editor(editor_id))
        await self._cache.delete(*keys)

    async def get_by_id(self, editor_id: int) -> EditorOut:
        ck = cache_keys.editor(editor_id)
        cached = await self._cache.get_json(ck)
        if cached is not None:
            return EditorOut.model_validate(cached)
        editor = await self._repo.get_by_id(editor_id)
        if editor is None:
            raise EntityNotFoundException("Editor", editor_id)
        dto = EditorOut.model_validate(editor)
        await self._cache.set_json(ck, dto.model_dump(mode="json"), ttl_seconds=self._ttl)
        return dto

    async def get_all(self) -> list[EditorOut]:
        ck = cache_keys.editors_all()
        cached = await self._cache.get_json(ck)
        if cached is not None:
            return [EditorOut.model_validate(x) for x in cached]
        editors = await self._repo.get_all()
        out = [EditorOut.model_validate(e) for e in editors]
        await self._cache.set_json(
            ck,
            [e.model_dump(mode="json") for e in out],
            ttl_seconds=self._ttl,
        )
        return out

    async def create(self, data: EditorRequestTo) -> EditorOut:
        existing = await self._repo.get_by_login(data.login)
        if existing is not None:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        role = data.role if data.role is not None else UserRole.CUSTOMER
        editor = Editor(
            login=data.login,
            password=hash_password(data.password),
            firstname=data.firstname,
            lastname=data.lastname,
            role=role,
        )
        created = await self._repo.create(editor)
        await self._uow.commit()
        dto = EditorOut.model_validate(created)
        await self._invalidate_editors()
        await self._cache.set_json(
            cache_keys.editor(dto.id),
            dto.model_dump(mode="json"),
            ttl_seconds=self._ttl,
        )
        return dto

    async def update(self, editor_id: int, data: EditorRequestTo) -> EditorOut:
        existing = await self._repo.get_by_login(data.login)
        if existing is not None and existing.id != editor_id:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        role = data.role if data.role is not None else UserRole.CUSTOMER
        editor = Editor(
            login=data.login,
            password=hash_password(data.password),
            firstname=data.firstname,
            lastname=data.lastname,
            role=role,
        )
        editor.id = editor_id
        updated = await self._repo.update(editor)
        if updated is None:
            raise EntityNotFoundException("Editor", editor_id)
        await self._uow.commit()
        dto = EditorOut.model_validate(updated)
        await self._invalidate_editors(editor_id)
        await self._cache.set_json(
            cache_keys.editor(editor_id),
            dto.model_dump(mode="json"),
            ttl_seconds=self._ttl,
        )
        return dto

    async def delete(self, editor_id: int) -> None:
        deleted = await self._repo.delete(editor_id)
        if not deleted:
            raise EntityNotFoundException("Editor", editor_id)
        await self._uow.commit()
        await self._invalidate_editors(editor_id)
