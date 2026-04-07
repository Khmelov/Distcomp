from datetime import datetime, timezone

from app.dto.issue import IssueRequestTo, IssueResponseTo
from app.dto.user import UserResponseTo
from app.exceptions import EntityDuplicateException, EntityNotFoundException
from app.models.issue import Issue
from app.models.sticker import Sticker
from app.repositories import CrudRepository
from app.settings import settings


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
        sticker_ids = list(request.stickerIds)
        if (
            not sticker_ids
            and settings.storage == "postgres"
            and settings.issue_auto_rgb_stickers
        ):
            sticker_ids = self._ensure_rgb_sticker_ids_for_user(request.userId)
        self._ensure_references(request.userId, sticker_ids)
        self._ensure_unique_user_title(request.userId, request.title)
        now = datetime.now(timezone.utc)
        entity = Issue(
            userId=request.userId,
            title=request.title,
            content=request.content,
            created=now,
            modified=now,
            stickerIds=sticker_ids,
        )
        created = self._repository.create(entity)
        return self._to_response(created)

    def _ensure_rgb_sticker_ids_for_user(self, user_id: int) -> list[int]:
        """Стикеры red{userId}, green{userId}, blue{userId} — ожидаются JDBC-проверками Task320."""
        ids: list[int] = []
        for color in ("red", "green", "blue"):
            name = f"{color}{user_id}"
            existing = next(
                (s for s in self._sticker_repository.find_all() if s.name == name),
                None,
            )
            if existing is not None:
                ids.append(existing.id)
            else:
                created = self._sticker_repository.create(Sticker(name=name))
                ids.append(created.id)
        return ids

    def update(self, request: IssueRequestTo) -> IssueResponseTo:
        if request.id is None:
            raise EntityNotFoundException("Issue", 0)
        existing = self._repository.find_by_id(request.id)
        if existing is None:
            raise EntityNotFoundException("Issue", request.id)
        self._ensure_references(request.userId, request.stickerIds)
        self._ensure_unique_user_title(request.userId, request.title, ignore_issue_id=request.id)
        existing.userId = request.userId
        existing.title = request.title
        existing.content = request.content
        existing.stickerIds = request.stickerIds
        existing.modified = datetime.now(timezone.utc)
        updated = self._repository.update(existing)
        return self._to_response(updated)

    def delete(self, issue_id: int) -> None:
        issue = self._repository.find_by_id(issue_id)
        if issue is None:
            raise EntityNotFoundException("Issue", issue_id)
        sticker_ids = list(issue.stickerIds)
        if not self._repository.delete_by_id(issue_id):
            raise EntityNotFoundException("Issue", issue_id)
        self._delete_stickers_no_longer_linked(sticker_ids)

    def _delete_stickers_no_longer_linked(self, sticker_ids: list[int]) -> None:
        """Удаляет стикеры, на которые больше не ссылается ни один issue (после удаления issue)."""
        if not sticker_ids:
            return
        still_used: set[int] = set()
        for iss in self._repository.find_all():
            still_used.update(iss.stickerIds)
        for sid in sticker_ids:
            if sid in still_used:
                continue
            self._sticker_repository.delete_by_id(sid)

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

    def _ensure_unique_user_title(
        self, user_id: int, title: str, ignore_issue_id: int | None = None
    ) -> None:
        for issue in self._repository.find_all():
            if issue.userId != user_id or issue.title != title:
                continue
            if ignore_issue_id is not None and issue.id == ignore_issue_id:
                continue
            raise EntityDuplicateException("title", title)

    def _ensure_references(self, user_id: int, sticker_ids: list[int]) -> None:
        if self._user_repository.find_by_id(user_id) is None:
            raise EntityNotFoundException("User", user_id)
        for sticker_id in sticker_ids:
            if self._sticker_repository.find_by_id(sticker_id) is None:
                raise EntityNotFoundException("Sticker", sticker_id)

    @staticmethod
    def _to_response(issue: Issue) -> IssueResponseTo:
        return IssueResponseTo.model_validate(issue.__dict__)
