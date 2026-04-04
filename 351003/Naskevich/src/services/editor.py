from src.database.uow import UnitOfWork
from src.dto.editor import EditorRequestTo, EditorResponseTo
from src.exceptions import EntityAlreadyExistsException, EntityNotFoundException
from src.models.editor import Editor
from src.repositories.editor import AbstractEditorRepository


class EditorService:

    def __init__(self, repository: AbstractEditorRepository, uow: UnitOfWork) -> None:
        self._repo = repository
        self._uow = uow

    async def get_by_id(self, editor_id: int) -> EditorResponseTo:
        editor = await self._repo.get_by_id(editor_id)
        if editor is None:
            raise EntityNotFoundException("Editor", editor_id)
        return EditorResponseTo.model_validate(editor)

    async def get_all(self) -> list[EditorResponseTo]:
        editors = await self._repo.get_all()
        return [EditorResponseTo.model_validate(e) for e in editors]

    async def create(self, data: EditorRequestTo) -> EditorResponseTo:
        existing = await self._repo.get_by_login(data.login)
        if existing is not None:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        editor = Editor(
            login=data.login,
            password=data.password,
            firstname=data.firstname,
            lastname=data.lastname,
        )
        created = await self._repo.create(editor)
        await self._uow.commit()
        return EditorResponseTo.model_validate(created)

    async def update(self, editor_id: int, data: EditorRequestTo) -> EditorResponseTo:
        existing = await self._repo.get_by_login(data.login)
        if existing is not None and existing.id != editor_id:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        editor = Editor(
            login=data.login,
            password=data.password,
            firstname=data.firstname,
            lastname=data.lastname,
        )
        editor.id = editor_id
        updated = await self._repo.update(editor)
        if updated is None:
            raise EntityNotFoundException("Editor", editor_id)
        await self._uow.commit()
        return EditorResponseTo.model_validate(updated)

    async def delete(self, editor_id: int) -> None:
        deleted = await self._repo.delete(editor_id)
        if not deleted:
            raise EntityNotFoundException("Editor", editor_id)
        await self._uow.commit()