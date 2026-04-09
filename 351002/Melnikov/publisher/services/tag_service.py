from ..common.errors import ConflictError, NotFoundError
from ..common.validation import validate_length
from ..dto.tag_dto import TagRequestTo, TagResponseTo
from ..models.tag import Tag
from ..repositories.registry import tag_repository


class TagService:
    def create(self, dto: TagRequestTo) -> TagResponseTo:
        name = validate_length("name", dto.name, 2, 32, 21)

        existing = [t for t in tag_repository.get_all() if t.name == name]
        if existing:
            raise ConflictError("Tag with this name already exists", 2)

        tag = Tag(id=0, name=name)
        created = tag_repository.create(tag)
        return self._to_response(created)

    def get_all(self) -> list[TagResponseTo]:
        return [self._to_response(tag) for tag in tag_repository.get_all()]

    def get_by_id(self, tag_id: int) -> TagResponseTo:
        tag = tag_repository.get_by_id(tag_id)
        if not tag:
            raise NotFoundError("Tag not found", 3)
        return self._to_response(tag)

    def update(self, tag_id: int, dto: TagRequestTo) -> TagResponseTo:
        tag = tag_repository.get_by_id(tag_id)
        if not tag:
            raise NotFoundError("Tag not found", 3)

        name = validate_length("name", dto.name, 2, 32, 21)

        for item in tag_repository.get_all():
            if item.name == name and item.id != tag_id:
                raise ConflictError("Tag with this name already exists", 2)

        tag.name = name
        updated = tag_repository.update(tag)
        return self._to_response(updated)

    def delete(self, tag_id: int) -> None:
        deleted = tag_repository.delete(tag_id)
        if not deleted:
            raise NotFoundError("Tag not found", 3)

    def get_many_by_ids(self, ids: list[int]) -> list[TagResponseTo]:
        result = []
        for tag_id in ids:
            tag = tag_repository.get_by_id(tag_id)
            if tag:
                result.append(self._to_response(tag))
        return result

    @staticmethod
    def _to_response(tag: Tag) -> TagResponseTo:
        return TagResponseTo(
            id=tag.id,
            name=tag.name
        )