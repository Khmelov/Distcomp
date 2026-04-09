from src.services.base import BaseCRUDService
from src.models import Issue, Editor, Label
from src.schemas.dto import IssueRequestTo, IssueResponseTo, EditorResponseTo, LabelResponseTo, PostResponseTo
from src.core.exceptions import BaseAppException


class IssueService(BaseCRUDService[Issue, IssueRequestTo, IssueRequestTo, IssueResponseTo]):
    def __init__(self):
        super().__init__(Issue, IssueResponseTo)

    async def create(self, create_dto: IssueRequestTo) -> IssueResponseTo:
        editor = await Editor.get_or_none(id=create_dto.editor_id)
        if not editor:
            raise BaseAppException(400, "40003", f"Editor with id {create_dto.editor_id} not found")

        issue = await self.model.create(
            title=create_dto.title, content=create_dto.content, editor_id=create_dto.editor_id
        )

        if create_dto.label_ids:
            labels = await Label.filter(id__in=create_dto.label_ids)
            if len(labels) != len(create_dto.label_ids):
                raise BaseAppException(400, "40005", "One or more labels not found")
            await issue.labels.add(*labels)

        return self.response_schema.model_validate(issue)

    async def update(self, obj_id: int, update_dto: IssueRequestTo) -> IssueResponseTo:
        issue = await self.model.get_or_none(id=obj_id)
        if not issue:
            raise BaseAppException(404, "40403", "Issue not found")

        editor = await Editor.get_or_none(id=update_dto.editor_id)
        if not editor:
            raise BaseAppException(400, "40003", "Editor not found")

        issue.title = update_dto.title
        issue.content = update_dto.content
        issue.editor_id = update_dto.editor_id
        await issue.save()

        if update_dto.label_ids is not None:
            labels = await Label.filter(id__in=update_dto.label_ids)
            await issue.labels.clear()
            await issue.labels.add(*labels)

        return self.response_schema.model_validate(issue)

    async def get_editor_by_issue(self, issue_id: int) -> EditorResponseTo:
        issue = await self.model.get_or_none(id=issue_id).prefetch_related("editor")
        if not issue:
            raise BaseAppException(404, "40403", "Issue not found")
        return EditorResponseTo.model_validate(issue.editor)

    async def get_labels_by_issue(self, issue_id: int) -> list[LabelResponseTo]:
        issue = await self.model.get_or_none(id=issue_id).prefetch_related("labels")
        if not issue:
            raise BaseAppException(404, "40403", "Issue not found")
        return [LabelResponseTo.model_validate(label) for label in issue.labels]

    async def get_posts_by_issue(self, issue_id: int) -> list[PostResponseTo]:
        issue = await self.model.get_or_none(id=issue_id).prefetch_related("posts")
        if not issue:
            raise BaseAppException(404, "40403", "Issue not found")
        return [PostResponseTo.model_validate(post) for post in issue.posts]

    async def search_issues(
        self,
        label_names: list[str] | None = None,
        label_ids: list[int] | None = None,
        editor_login: str | None = None,
        title: str | None = None,
        content: str | None = None,
    ) -> list[IssueResponseTo]:

        query = self.model.all()

        if label_names:
            query = query.filter(labels__name__in=label_names)
        if label_ids:
            query = query.filter(labels__id__in=label_ids)
        if editor_login:
            query = query.filter(editor__login=editor_login)
        if title:
            query = query.filter(title__icontains=title)
        if content:
            query = query.filter(content__icontains=content)

        issues = await query.distinct()
        return [self.response_schema.model_validate(issue) for issue in issues]
