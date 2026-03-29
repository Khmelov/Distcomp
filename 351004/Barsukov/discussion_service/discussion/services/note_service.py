from discussion.models.note_model import NoteModel
from discussion.schemas.note import NoteRequestTo, NoteResponseTo
from fastapi import HTTPException, status
from typing import List


class NoteService:
    def __init__(self, session):
        self.model = NoteModel(session)

    def create(self, dto: NoteRequestTo) -> NoteResponseTo:
        """Создание новой заметки"""
        data = dto.model_dump(exclude_none=True)

        # Преобразуем camelCase в snake_case
        if "issueId" in data:
            data["issue_id"] = data.pop("issueId")

        # Устанавливаем начальный статус
        if "state" not in data:
            data["state"] = "PENDING"

        # Генерируем ID если не указан
        if "id" not in data or data["id"] is None:
            data["id"] = self.model.get_next_id()

        result = self.model.create(data)
        if not result:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to create note"
            )

        return NoteResponseTo(
            id=result['id'],
            issueId=result['issue_id'],
            content=result['content'],
            state=result['state']
        )

    def get_all(self) -> List[NoteResponseTo]:
        """Получение всех заметок (может быть медленно)"""
        results = self.model.get_all()
        return [
            NoteResponseTo(
                id=r['id'],
                issueId=r['issue_id'],
                content=r['content'],
                state=r['state']  # Добавлено
            ) for r in results
        ]

    def get_by_issue_and_id(self, issue_id: int, note_id: int) -> NoteResponseTo:
        """Получение заметки по issue_id и note_id (эффективно)"""
        result = self.model.get_by_id(issue_id, note_id)
        if not result:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Note not found"
            )

        return NoteResponseTo(
            id=result['id'],
            issueId=result['issue_id'],
            content=result['content'],
            state=result['state']  # Добавлено
        )

    def get_by_issue(self, issue_id: int) -> List[NoteResponseTo]:
        """Все заметки конкретного issue (эффективно)"""
        results = self.model.get_all_by_issue(issue_id)
        return [
            NoteResponseTo(
                id=r['id'],
                issueId=r['issue_id'],
                content=r['content'],
                state=r['state']  # Добавлено
            ) for r in results
        ]

    def update_state(self, issue_id: int, note_id: int, state: str) -> NoteResponseTo:
        """Обновление статуса заметки"""
        note = self.model.get_by_id(issue_id, note_id)
        if not note:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="Note not found"
            )

        result = self.model.update(issue_id, note_id, note['content'], state)
        return NoteResponseTo(
            id=result['id'],
            issueId=result['issue_id'],
            content=result['content'],
            state=result['state']
        )

    def delete(self, issue_id: int, note_id: int) -> bool:
        """Удаление заметки"""
        return self.model.delete(issue_id, note_id)