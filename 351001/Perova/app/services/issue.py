from datetime import datetime, timezone

from app.dto.issue import IssueRequestTo, IssueResponseTo
from app.dto.user import UserResponseTo
from app.exceptions import EntityNotFoundException
from app.models.issue import Issue
from app.models.sticker import Sticker
from app.repositories import CrudRepository


class IssueService:
    def __init__(
        self,
        repository: CrudRepository[Issue],
        user_repository: CrudRepository,
        sticker_repository: CrudRepository[Sticker],
    ) -> None:
        self._repository = repository
        self._user_repository = user_repository
        self._sticker_repository = sticker_repository

    def get_all(self) -> list[IssueResponseTo]:
        return [self._to_response(issue) for issue in self._repository.find_all()]

    def get_by_id(self, issue_id: int) -> IssueResponseTo:
        issue = self._repository.find_by_id(issue_id)
        if issue is None:
            raise EntityNotFoundException("Issue", issue_id)
        return self._to_response(issue)

    def get_entity_by_id(self, issue_id: int) -> Issue:
        issue = self._repository.find_by_id(issue_id)
        if issue is None:
            raise EntityNotFoundException("Issue", issue_id)
        return issue

    def create(self, request: IssueRequestTo) -> IssueResponseTo:
        self._ensure_references(request.userId, request.stickerIds)
        now = datetime.now(timezone.utc)
        entity = Issue(
            userId=request.userId,
            title=request.title,
            content=request.content,
            created=now,
            modified=now,
            stickerIds=request.stickerIds,
        )
        created = self._repository.create(entity)
        return self._to_response(created)

    def update(self, request: IssueRequestTo) -> IssueResponseTo:
        if request.id is None:
            raise EntityNotFoundException("Issue", 0)
        existing = self._repository.find_by_id(request.id)
        if existing is None:
            raise EntityNotFoundException("Issue", request.id)
        self._ensure_references(request.userId, request.stickerIds)
        existing.userId = request.userId
        existing.title = request.title
        existing.content = request.content
        existing.stickerIds = request.stickerIds
        existing.modified = datetime.now(timezone.utc)
        updated = self._repository.update(existing)
        return self._to_response(updated)

    def delete(self, issue_id: int) -> None:
        if not self._repository.delete_by_id(issue_id):
            raise EntityNotFoundException("Issue", issue_id)

    def get_user_by_issue_id(self, issue_id: int) -> UserResponseTo:
        issue = self.get_entity_by_id(issue_id)
        user = self._user_repository.find_by_id(issue.userId)
        if user is None:
            raise EntityNotFoundException("User", issue.userId)
        return UserResponseTo(
            id=user.id,
            login=user.login,
            password=user.password,
            firstname=user.firstname,
            lastname=user.lastname,
        )

    def get_stickers_by_issue_id(self, issue_id: int) -> list[dict]:
        issue = self.get_entity_by_id(issue_id)
        result = []
        for sticker_id in issue.stickerIds:
            sticker = self._sticker_repository.find_by_id(sticker_id)
            if sticker is not None:
                result.append({"id": sticker.id, "name": sticker.name})
        return result

    def search(
        self,
        sticker_names: list[str] | None = None,
        sticker_ids: list[int] | None = None,
        user_login: str | None = None,
        title: str | None = None,
        content: str | None = None,
    ) -> list[IssueResponseTo]:
        sticker_names = sticker_names or []
        sticker_ids = sticker_ids or []

        user_id = None
        if user_login:
            user = next((u for u in self._user_repository.find_all() if u.login == user_login), None)
            if user is None:
                return []
            user_id = user.id

        name_to_id = {st.name: st.id for st in self._sticker_repository.find_all()}
        sticker_ids_from_names = [name_to_id[name] for name in sticker_names if name in name_to_id]
        required_sticker_ids = set(sticker_ids + sticker_ids_from_names)

        issues = self._repository.find_all()
        filtered: list[Issue] = []
        for issue in issues:
            if user_id is not None and issue.userId != user_id:
                continue
            if title and title.lower() not in issue.title.lower():
                continue
            if content and content.lower() not in issue.content.lower():
                continue
            if required_sticker_ids and not required_sticker_ids.issubset(set(issue.stickerIds)):
                continue
            filtered.append(issue)
        return [self._to_response(item) for item in filtered]

    def _ensure_references(self, user_id: int, sticker_ids: list[int]) -> None:
        if self._user_repository.find_by_id(user_id) is None:
            raise EntityNotFoundException("User", user_id)
        for sticker_id in sticker_ids:
            if self._sticker_repository.find_by_id(sticker_id) is None:
                raise EntityNotFoundException("Sticker", sticker_id)

    @staticmethod
    def _to_response(issue: Issue) -> IssueResponseTo:
        return IssueResponseTo.model_validate(issue.__dict__)
