from typing import List
from sqlalchemy.orm import Session
from app.models import Editor
from app.repository import BaseRepository
from app.schemas.editor import EditorCreate, EditorUpdate, EditorResponse
from app.core.exceptions import EntityNotFoundException, EntityAlreadyExistsException
from app.auth import hash_password


class EditorService:
    def __init__(self, db: Session):
        self.repo = BaseRepository(Editor, db)

    def get_all(self, page=0, size=10000, sort_by="id", sort_order="asc") -> List[EditorResponse]:
        editors = self.repo.get_all(page=page, size=size, sort_by=sort_by, sort_order=sort_order)
        return [EditorResponse.model_validate(e) for e in editors]

    def get_by_id(self, editor_id: int) -> EditorResponse:
        editor = self.repo.get_by_id(editor_id)
        if not editor:
            raise EntityNotFoundException("Editor", editor_id)
        return EditorResponse.model_validate(editor)

    def get_by_login(self, login: str) -> Editor:
        return self.repo.get_by_field("login", login)

    def create(self, data: EditorCreate) -> EditorResponse:
        existing = self.repo.get_by_field("login", data.login)
        if existing:
            raise EntityAlreadyExistsException("Editor", "login", data.login)
        editor = Editor(
            login=data.login,
            password=hash_password(data.password),
            firstname=data.firstname,
            lastname=data.lastname,
            role=getattr(data, "role", "CUSTOMER") or "CUSTOMER",
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
        editor.password = hash_password(data.password)
        editor.firstname = data.firstname
        editor.lastname = data.lastname
        editor.role = getattr(data, "role", "CUSTOMER") or "CUSTOMER"
        updated = self.repo.update(editor)
        return EditorResponse.model_validate(updated)

    def delete(self, editor_id: int) -> None:
        editor = self.repo.get_by_id(editor_id)
        if not editor:
            raise EntityNotFoundException("Editor", editor_id)
        self.repo.delete(editor)
