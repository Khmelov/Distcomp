from app.repository.memory import note_repo, issue_repo
from app.schemas.note import NoteRequestTo, NoteResponseTo
from app.core.exceptions import AppException


class NoteService:
    def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        # Проверяем, что issue существует
        if not issue_repo.find_by_id(dto.issueId):
            raise AppException(400, "Issue not found", 12)
        return NoteResponseTo(**note_repo.save(dto.model_dump()))

    def get_all(self):
        return [NoteResponseTo(**n) for n in note_repo.find_all()]

    def get_by_id(self, id: int):
        res = note_repo.find_by_id(id)
        if not res:
            raise AppException(404, "Note not found", 13)
        return NoteResponseTo(**res)

    def update(self, id: int, dto: NoteRequestTo) -> NoteResponseTo:
        # Проверяем, что note существует
        if not note_repo.find_by_id(id):
            raise AppException(404, "Note not found", 14)
        # Проверяем, что issue существует
        if not issue_repo.find_by_id(dto.issueId):
            raise AppException(400, "Issue not found", 15)
        data = dto.model_dump()
        data["id"] = id
        saved = note_repo.save(data)
        return NoteResponseTo(**saved)

    def delete(self, id: int):
        if not note_repo.delete(id):
            raise AppException(404, "Note not found", 16)
