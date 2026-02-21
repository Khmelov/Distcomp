from datetime import datetime

from app.repository.memory import issue_repo, author_repo
from app.schemas.issue import IssueRequestTo, IssueResponseTo
from app.core.exceptions import AppException


class IssueService:
    def create(self, dto: IssueRequestTo) -> IssueResponseTo:
        # Проверяем, что автор существует
        if not author_repo.find_by_id(dto.authorId):
            raise AppException(400, "Author not found", 4)

        now = datetime.now().isoformat()
        data = dto.model_dump()
        data.update({"created": now, "modified": now})

        return IssueResponseTo(**issue_repo.save(data))

    def get_all(self):
        return [IssueResponseTo(**i) for i in issue_repo.find_all()]

    def get_by_id(self, id: int):
        res = issue_repo.find_by_id(id)
        if not res:
            raise AppException(404, "Issue not found", 5)
        return IssueResponseTo(**res)

    def update(self, id: int, dto: IssueRequestTo) -> IssueResponseTo:
        # Автор должен существовать
        if not author_repo.find_by_id(dto.authorId):
            raise AppException(400, "Author not found", 7)

        existing = issue_repo.find_by_id(id)
        if not existing:
            raise AppException(404, "Issue not found", 6)

        now = datetime.now().isoformat()
        data = dto.model_dump()
        data["id"] = id
        data["created"] = existing.get("created", now)
        data["modified"] = now

        return IssueResponseTo(**issue_repo.save(data))

    def delete(self, id: int):
        if not issue_repo.delete(id):
            raise AppException(404, "Issue not found", 8)
