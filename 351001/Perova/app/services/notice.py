from collections.abc import Callable

from app.cache.codec import model_list_to_primitive, model_to_primitive
from app.cache.keys import CacheKeys
from app.dto.notice import NoticeRequestTo, NoticeResponseTo
from app.exceptions import EntityNotFoundException
from app.models.notice import Notice
from app.models.notice_state import NoticeState
from app.repositories import CrudRepository
from app.repositories.paging import PageRequest


class NoticeService:
    def __init__(
        self,
        repository: CrudRepository[Notice],
        issue_repository: CrudRepository,
        cache_getter: Callable[[], object | None] | None = None,
    ) -> None:
        self._repository = repository
        self._issue_repository = issue_repository
        self._cache_getter = cache_getter or (lambda: None)

    def get_all(self) -> list[NoticeResponseTo]:
        key = CacheKeys.notice_list()
        cached = self._cache_get(key)
        if isinstance(cached, list):
            return [NoticeResponseTo.model_validate(item) for item in cached]
        result = [self._to_response(notice) for notice in self._repository.find_all()]
        self._cache_set(key, model_list_to_primitive(result))
        return result

    def get_by_id(self, notice_id: int) -> NoticeResponseTo:
        key = CacheKeys.notice_id(notice_id)
        cached = self._cache_get(key)
        if isinstance(cached, dict):
            return NoticeResponseTo.model_validate(cached)
        notice = self._repository.find_by_id(notice_id)
        if notice is None:
            raise EntityNotFoundException("Notice", notice_id)
        response = self._to_response(notice)
        self._cache_set(key, model_to_primitive(response))
        return response

    def get_by_issue_id(self, issue_id: int) -> list[NoticeResponseTo]:
        key = CacheKeys.issue_notices(issue_id)
        cached = self._cache_get(key)
        if isinstance(cached, list):
            return [NoticeResponseTo.model_validate(item) for item in cached]
        if self._issue_repository.find_by_id(issue_id) is None:
            raise EntityNotFoundException("Issue", issue_id)
        result = self._repository.find_page(
            PageRequest(page=0, size=10**9, sort=[("id", True)]),
            filters={"issueId": issue_id},
        )
        response = [self._to_response(n) for n in result.items]
        self._cache_set(key, model_list_to_primitive(response))
        return response

    def create(self, request: NoticeRequestTo) -> NoticeResponseTo:
        self._ensure_issue_exists(request.issueId)
        created = self._repository.create(
            Notice(issueId=request.issueId, content=request.content, state=NoticeState.PENDING)
        )
        response = self._to_response(created)
        self._invalidate_notice_cache(response.id, response.issueId)
        return response

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
        response = self._to_response(updated)
        self._invalidate_notice_cache(response.id, response.issueId)
        return response

    def delete(self, notice_id: int) -> None:
        existing = self._repository.find_by_id(notice_id)
        issue_id = existing.issueId if existing is not None else None
        if not self._repository.delete_by_id(notice_id):
            raise EntityNotFoundException("Notice", notice_id)
        self._invalidate_notice_cache(notice_id, issue_id)

    def _ensure_issue_exists(self, issue_id: int) -> None:
        if self._issue_repository.find_by_id(issue_id) is None:
            raise EntityNotFoundException("Issue", issue_id)

    @staticmethod
    def _to_response(notice: Notice) -> NoticeResponseTo:
        return NoticeResponseTo.model_validate(notice.__dict__)

    def _invalidate_notice_cache(self, notice_id: int | None = None, issue_id: int | None = None) -> None:
        self._cache_delete(CacheKeys.notice_list())
        if notice_id is not None:
            self._cache_delete(CacheKeys.notice_id(notice_id))
        if issue_id is not None:
            self._cache_delete(CacheKeys.issue_notices(issue_id))
        self._cache_delete_pattern(f"{CacheKeys.PREFIX}:issue:id:*:notices")

    def _cache_get(self, key: str):
        cache = self._cache_getter()
        if cache is None:
            return None
        return cache.get_json(key)

    def _cache_set(self, key: str, value: object) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.set_json(key, value)

    def _cache_delete(self, key: str) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.delete(key)

    def _cache_delete_pattern(self, pattern: str) -> None:
        cache = self._cache_getter()
        if cache is None:
            return
        cache.delete_by_pattern(pattern)
