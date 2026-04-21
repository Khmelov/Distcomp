from typing import List, Optional
from sqlalchemy.orm import Session
from app.models import Editor
from app.repository import BaseRepository
from app.schemas.editor import EditorCreate, EditorUpdate, EditorResponse
from app.core.exceptions import EntityNotFoundException, EntityAlreadyExistsException


class EditorService:
    def __init__(self, db: Session):
        self.repo = BaseRepository(Editor, db)

    def get_all(
        self,
        page: int = 0,
        size: int = 10,
        sort_by: str = "id",
        sort_order: str = "asc"
    ) -> List[EditorResponse]:
        editors = self.repo.get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
        return [EditorResponse.model_validate(e) for e in editors]

    def get_by_id(self, editor_id: int) -> EditorResponse:
        editor = self.repo.get_by_id(editor_id)
        if not editor:
            raise EntityNotFoundException("Editor", editor_id)
        return EditorResponse.model_validate(editor)

    def create(self, data: EditorCreate) -> EditorResponse:
        existing = self.repo.get_by_field("login", data.login)
        if existing:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        editor = Editor(
            login=data.login,
            password=data.password,
            firstname=data.firstname,
            lastname=data.lastname
        )
        created = self.repo.create(editor)
        return EditorResponse.model_validate(created)

    def update(self, data: EditorUpdate) -> EditorResponse:
        editor = self.repo.get_by_id(data.id)
        if not editor:
            raise EntityNotFoundException("Editor", data.id)
        existing = self.repo.get_by_field("login", data.login)
        if existing and existing.id != data.id:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        editor.login = data.login
        editor.password = data.password
        editor.firstname = data.firstname
        editor.lastname = data.lastname
        updated = self.repo.update(editor)
        return EditorResponse.model_validate(updated)

    def delete(self, editor_id: int) -> None:
        editor = self.repo.get_by_id(editor_id)
        if not editor:
            raise EntityNotFoundException("Editor", editor_id)
        self.repo.delete(editor)
