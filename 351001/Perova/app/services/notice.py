from app.dto.notice import NoticeRequestTo, NoticeResponseTo
from app.exceptions import EntityNotFoundException
from app.models.notice import Notice
from app.models.notice_state import NoticeState
from app.repositories import CrudRepository
from app.repositories.paging import PageRequest


class NoticeService:
    def __init__(self, repository: CrudRepository[Notice], issue_repository: CrudRepository) -> None:
        self._repository = repository
        self._issue_repository = issue_repository

    def get_all(self) -> list[NoticeResponseTo]:
        return [self._to_response(notice) for notice in self._repository.find_all()]

    def get_by_id(self, notice_id: int) -> NoticeResponseTo:
        notice = self._repository.find_by_id(notice_id)
        if notice is None:
            raise EntityNotFoundException("Notice", notice_id)
        return self._to_response(notice)

    def get_by_issue_id(self, issue_id: int) -> list[NoticeResponseTo]:
        if self._issue_repository.find_by_id(issue_id) is None:
            raise EntityNotFoundException("Issue", issue_id)
        result = self._repository.find_page(
            PageRequest(page=0, size=10**9, sort=[("id", True)]),
            filters={"issueId": issue_id},
        )
        return [self._to_response(n) for n in result.items]

    def create(self, request: NoticeRequestTo) -> NoticeResponseTo:
        self._ensure_issue_exists(request.issueId)
        created = self._repository.create(
            Notice(issueId=request.issueId, content=request.content, state=NoticeState.PENDING)
        )
        return self._to_response(created)

    def update(self, request: NoticeRequestTo) -> NoticeResponseTo:
        if request.id is None:
            raise EntityNotFoundException("Notice", 0)
        existing = self._repository.find_by_id(request.id)
        if existing is None:
            raise EntityNotFoundException("Notice", request.id)
        self._ensure_issue_exists(request.issueId)
        existing.issueId = request.issueId
        existing.content = request.content
        updated = self._repository.update(existing)
        return self._to_response(updated)

    def delete(self, notice_id: int) -> None:
        if not self._repository.delete_by_id(notice_id):
            raise EntityNotFoundException("Notice", notice_id)

    def _ensure_issue_exists(self, issue_id: int) -> None:
        if self._issue_repository.find_by_id(issue_id) is None:
            raise EntityNotFoundException("Issue", issue_id)

    @staticmethod
    def _to_response(notice: Notice) -> NoticeResponseTo:
        return NoticeResponseTo.model_validate(notice.__dict__)
