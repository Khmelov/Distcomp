from tortoise.exceptions import IntegrityError
from src.services.base import BaseCRUDService
from src.models import Editor
from src.schemas.dto import EditorRequestTo, EditorResponseTo
from src.core.security import get_password_hash
from src.core.exceptions import BaseAppException
from src.core.cache import invalidate_cache_by_prefix


class EditorService(BaseCRUDService[Editor, EditorRequestTo, EditorRequestTo, EditorResponseTo]):
    def __init__(self):
        super().__init__(Editor, EditorResponseTo)

    async def create(self, create_dto: EditorRequestTo) -> EditorResponseTo:
        try:
            data = create_dto.model_dump(exclude_unset=True)
            data["password"] = get_password_hash(data["password"])

            obj = await self.model.create(**data)
            await obj.refresh_from_db()
        except IntegrityError as e:
            raise BaseAppException(403, "40301", f"Validation Error: {str(e)}")

        result = self.response_schema.model_validate(obj)
        await invalidate_cache_by_prefix(self.cache_prefix)
        return result

    async def update(self, obj_id: int, update_dto: EditorRequestTo) -> EditorResponseTo:
        obj = await self.model.get_or_none(id=obj_id)
        if not obj:
            raise BaseAppException(404, "40402", f"Editor not found")
        try:
            data = update_dto.model_dump(exclude_unset=True)
            if "password" in data and not data["password"].startswith("$2"):
                data["password"] = get_password_hash(data["password"])

            await obj.update_from_dict(data).save()
            await obj.refresh_from_db()
        except IntegrityError as e:
            raise BaseAppException(403, "40301", f"Validation Error: {str(e)}")

        result = self.response_schema.model_validate(obj)
        await invalidate_cache_by_prefix(self.cache_prefix)
        return result