from datetime import datetime, timezone

from ..common.errors import NotFoundError
from ..common.validation import validate_length
from ..dto.author_dto import AuthorResponseTo
from ..dto.comment_dto import CommentResponseTo
from ..dto.issue_dto import IssueRequestTo, IssueResponseTo
from ..dto.tag_dto import TagResponseTo
from ..models.issue import Issue
from ..repositories.registry import (
    author_repository,
    issue_repository,
    tag_repository,
    comment_repository
)


class IssueService:
    def create(self, dto: IssueRequestTo) -> IssueResponseTo:
        author = author_repository.get_by_id(dto.author_id)
        if not author:
            raise NotFoundError("Author not found", 1)

        title = validate_length("title", dto.title, 2, 64, 11)
        content = validate_length("content", dto.content, 4, 2048, 12)

        for tag_id in dto.tag_ids:
            if not tag_repository.get_by_id(tag_id):
                raise NotFoundError(f"Tag with id={tag_id} not found", 13)

        now = datetime.now(timezone.utc)
        issue = Issue(
            id=0,
            author_id=dto.author_id,
            title=title,
            content=content,
            created=now,
            modified=now,
            tag_ids=dto.tag_ids
        )
        created = issue_repository.create(issue)
        return self._to_response(created)

    def get_all(self) -> list[IssueResponseTo]:
        return [self._to_response(issue) for issue in issue_repository.get_all()]

    def get_by_id(self, issue_id: int) -> IssueResponseTo:
        issue = issue_repository.get_by_id(issue_id)
        if not issue:
            raise NotFoundError("Issue not found", 2)
        return self._to_response(issue)

    def update(self, issue_id: int, dto: IssueRequestTo) -> IssueResponseTo:
        issue = issue_repository.get_by_id(issue_id)
        if not issue:
            raise NotFoundError("Issue not found", 2)

        author = author_repository.get_by_id(dto.author_id)
        if not author:
            raise NotFoundError("Author not found", 1)

        title = validate_length("title", dto.title, 2, 64, 11)
        content = validate_length("content", dto.content, 4, 2048, 12)

        for tag_id in dto.tag_ids:
            if not tag_repository.get_by_id(tag_id):
                raise NotFoundError(f"Tag with id={tag_id} not found", 13)

        issue.author_id = dto.author_id
        issue.title = title
        issue.content = content
        issue.modified = datetime.now(timezone.utc)
        issue.tag_ids = dto.tag_ids

        updated = issue_repository.update(issue)
        return self._to_response(updated)

    def delete(self, issue_id: int) -> None:
        deleted = issue_repository.delete(issue_id)
        if not deleted:
            raise NotFoundError("Issue not found", 2)

    def get_author_by_issue_id(self, issue_id: int) -> AuthorResponseTo:
        issue = issue_repository.get_by_id(issue_id)
        if not issue:
            raise NotFoundError("Issue not found", 2)

        author = author_repository.get_by_id(issue.author_id)
        if not author:
            raise NotFoundError("Author not found", 1)

        return AuthorResponseTo(
            id=author.id,
            login=author.login,
            password=author.password,
            firstname=author.firstname,
            lastname=author.lastname
        )

    def get_tags_by_issue_id(self, issue_id: int) -> list[TagResponseTo]:
        issue = issue_repository.get_by_id(issue_id)
        if not issue:
            raise NotFoundError("Issue not found", 2)

        result = []
        for tag_id in issue.tag_ids:
            tag = tag_repository.get_by_id(tag_id)
            if tag:
                result.append(TagResponseTo(id=tag.id, name=tag.name))
        return result

    def get_comments_by_issue_id(self, issue_id: int) -> list[CommentResponseTo]:
        issue = issue_repository.get_by_id(issue_id)
        if not issue:
            raise NotFoundError("Issue not found", 2)

        result = []
        for comment in comment_repository.get_all():
            if comment.issue_id == issue_id:
                result.append(
                    CommentResponseTo(
                        id=comment.id,
                        issueId=comment.issue_id,
                        content=comment.content
                    )
                )
        return result

    def search(
        self,
        tag_ids: list[int] | None = None,
        title: str | None = None,
        content: str | None = None
    ) -> list[IssueResponseTo]:
        issues = issue_repository.get_all()

        if tag_ids:
            issues = [
                issue for issue in issues
                if all(tag_id in issue.tag_ids for tag_id in tag_ids)
            ]

        if title:
            title_lower = title.lower()
            issues = [
                issue for issue in issues
                if title_lower in issue.title.lower()
            ]

        if content:
            content_lower = content.lower()
            issues = [
                issue for issue in issues
                if content_lower in issue.content.lower()
            ]

        return [self._to_response(issue) for issue in issues]

    @staticmethod
    def _to_response(issue: Issue) -> IssueResponseTo:
        return IssueResponseTo(
            id=issue.id,
            authorId=issue.author_id,
            title=issue.title,
            content=issue.content,
            created=issue.created,
            modified=issue.modified,
            tagIds=issue.tag_ids
        )