import hashlib
from pydantic import TypeAdapter
from tortoise.exceptions import IntegrityError
from tortoise.transactions import in_transaction

from src.services.base import BaseCRUDService
from src.models import Issue, Editor, Label
from src.schemas.dto import IssueRequestTo, IssueResponseTo, EditorResponseTo, LabelResponseTo, PostResponseTo
from src.core.exceptions import BaseAppException
from src.services.post import PostService
from src.core.cache import get_cache, set_cache, invalidate_cache_by_prefix


class IssueService(BaseCRUDService[Issue, IssueRequestTo, IssueRequestTo, IssueResponseTo]):
    def __init__(self):
        super().__init__(Issue, IssueResponseTo)

    async def _cleanup_labels(self, labels_list: list[Label]):
        for label in labels_list:
            count = await label.issues.all().count()
            if count == 0:
                await label.delete()

    async def create(self, create_dto: IssueRequestTo) -> IssueResponseTo:
        editor = await Editor.get_or_none(id=create_dto.editor_id)
        if not editor: raise BaseAppException(400, "40003", f"Editor not found")

        async with in_transaction():
            try:
                issue = await self.model.create(
                    title=create_dto.title, content=create_dto.content, editor_id=create_dto.editor_id
                )
            except IntegrityError as e:
                raise BaseAppException(403, "40301", f"Validation Error: {str(e)}")

            if create_dto.label_ids:
                labels_by_id = await Label.filter(id__in=create_dto.label_ids)
                await issue.labels.add(*labels_by_id)

            if create_dto.labels:
                for label_name in create_dto.labels:
                    label_obj, _ = await Label.get_or_create(name=label_name)
                    await issue.labels.add(label_obj)

            result = self.response_schema.model_validate(issue)
            await invalidate_cache_by_prefix(self.cache_prefix)  # Очищаем кеш
            return result

    async def update(self, obj_id: int, update_dto: IssueRequestTo) -> IssueResponseTo:
        issue = await self.model.get_or_none(id=obj_id).prefetch_related("labels")
        if not issue: raise BaseAppException(404, "40403", "Issue not found")

        editor = await Editor.get_or_none(id=update_dto.editor_id)
        if not editor: raise BaseAppException(400, "40003", "Editor not found")

        async with in_transaction():
            old_labels = list(issue.labels)
            issue.title = update_dto.title
            issue.content = update_dto.content
            issue.editor_id = update_dto.editor_id

            try:
                await issue.save()
            except IntegrityError as e:
                raise BaseAppException(403, "40301", f"Validation Error: {str(e)}")

            await issue.labels.clear()

            if update_dto.label_ids:
                labels_by_id = await Label.filter(id__in=update_dto.label_ids)
                await issue.labels.add(*labels_by_id)

            if update_dto.labels:
                for label_name in update_dto.labels:
                    label_obj, _ = await Label.get_or_create(name=label_name)
                    await issue.labels.add(label_obj)

            await self._cleanup_labels(old_labels)

            result = self.response_schema.model_validate(issue)
            await invalidate_cache_by_prefix(self.cache_prefix)
            return result

    async def delete(self, obj_id: int) -> None:
        issue = await self.model.get_or_none(id=obj_id).prefetch_related("labels")
        if not issue: raise BaseAppException(404, "40403", "Issue not found")

        labels_to_check = list(issue.labels)
        async with in_transaction():
            await issue.delete()
            await self._cleanup_labels(labels_to_check)

        await invalidate_cache_by_prefix(self.cache_prefix)

    async def search_issues(
            self,
            label_names: list[str] | None = None,
            label_ids: list[int] | None = None,
            editor_login: str | None = None,
            title: str | None = None,
            content: str | None = None,
    ) -> list[IssueResponseTo]:

        params_str = f"{label_names}:{label_ids}:{editor_login}:{title}:{content}"
        params_hash = hashlib.md5(params_str.encode()).hexdigest()
        cache_key = f"{self.cache_prefix}:search:{params_hash}"

        cached = await get_cache(cache_key)
        if cached:
            adapter = TypeAdapter(list[IssueResponseTo])
            return adapter.validate_json(cached)

        query = self.model.all()
        if label_names: query = query.filter(labels__name__in=label_names)
        if label_ids: query = query.filter(labels__id__in=label_ids)
        if editor_login: query = query.filter(editor__login=editor_login)
        if title: query = query.filter(title__icontains=title)
        if content: query = query.filter(content__icontains=content)

        issues = await query.distinct()
        results = [self.response_schema.model_validate(issue) for issue in issues]

        adapter = TypeAdapter(list[IssueResponseTo])
        await set_cache(cache_key, adapter.dump_json(results), ex=3600)
        return results

    async def get_editor_by_issue(self, issue_id: int) -> EditorResponseTo:
        issue = await self.model.get_or_none(id=issue_id).prefetch_related("editor")
        if not issue: raise BaseAppException(404, "40403", "Issue not found")
        return EditorResponseTo.model_validate(issue.editor)

    async def get_labels_by_issue(self, issue_id: int) -> list[LabelResponseTo]:
        issue = await self.model.get_or_none(id=issue_id).prefetch_related("labels")
        if not issue: raise BaseAppException(404, "40403", "Issue not found")
        return [LabelResponseTo.model_validate(label) for label in issue.labels]

    async def get_posts_by_issue(self, issue_id: int) -> list[PostResponseTo]:
        issue = await self.model.get_or_none(id=issue_id)
        if not issue: raise BaseAppException(404, "40403", "Issue not found")
        post_service = PostService()
        return await post_service.get_all(issue_id=issue_id)