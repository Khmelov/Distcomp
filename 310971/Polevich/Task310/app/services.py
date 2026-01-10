from __future__ import annotations

from datetime import datetime, timezone
from typing import List, Optional

from app import dto
from app.exceptions import AppError, make_error_code
from app.models import Article, Creator, Message, Tag
from app.storage import BaseRepository


def _validate_length(value: str, min_len: int, max_len: int, field: str) -> None:
    if not (min_len <= len(value) <= max_len):
        raise AppError(
            status_code=400,
            error_message=f"{field} length must be between {min_len} and {max_len}",
            error_code=make_error_code(400, 1),
        )


class CreatorService:
    def __init__(self, repo: BaseRepository[Creator]) -> None:
        self.repo = repo

    def list_creators(self) -> List[dto.CreatorResponseTo]:
        return [self._to_response(c) for c in self.repo.list_all()]

    def get_creator(self, creator_id: int) -> dto.CreatorResponseTo:
        creator = self.repo.get(creator_id)
        if not creator:
            raise AppError(404, "Creator not found", make_error_code(404, 1))
        return self._to_response(creator)

    def create_creator(self, payload: dto.CreatorRequestTo) -> dto.CreatorResponseTo:
        self._validate_payload(payload)
        entity = Creator(
            id=0,
            login=payload.login,
            password=payload.password,
            firstname=payload.firstname,
            lastname=payload.lastname,
        )
        created = self.repo.create(entity)
        return self._to_response(created)

    def update_creator(self, creator_id: int, payload: dto.CreatorRequestTo) -> dto.CreatorResponseTo:
        self._validate_payload(payload)
        entity = Creator(
            id=creator_id,
            login=payload.login,
            password=payload.password,
            firstname=payload.firstname,
            lastname=payload.lastname,
        )
        updated = self.repo.update(creator_id, entity)
        return self._to_response(updated)

    def delete_creator(self, creator_id: int) -> None:
        self.repo.delete(creator_id)

    def _validate_payload(self, payload: dto.CreatorRequestTo) -> None:
        _validate_length(payload.login, 2, 64, "login")
        _validate_length(payload.password, 8, 128, "password")
        _validate_length(payload.firstname, 2, 64, "firstname")
        _validate_length(payload.lastname, 2, 64, "lastname")

    @staticmethod
    def _to_response(entity: Creator) -> dto.CreatorResponseTo:
        return dto.CreatorResponseTo(
            id=entity.id,
            login=entity.login,
            password=entity.password,
            firstname=entity.firstname,
            lastname=entity.lastname,
        )


class TagService:
    def __init__(self, repo: BaseRepository[Tag]) -> None:
        self.repo = repo

    def list_tags(self) -> List[dto.TagResponseTo]:
        return [self._to_response(t) for t in self.repo.list_all()]

    def get_tag(self, tag_id: int) -> dto.TagResponseTo:
        tag = self.repo.get(tag_id)
        if not tag:
            raise AppError(404, "Tag not found", make_error_code(404, 1))
        return self._to_response(tag)

    def create_tag(self, payload: dto.TagRequestTo) -> dto.TagResponseTo:
        _validate_length(payload.name, 2, 32, "name")
        entity = Tag(id=0, name=payload.name)
        created = self.repo.create(entity)
        return self._to_response(created)

    def update_tag(self, tag_id: int, payload: dto.TagRequestTo) -> dto.TagResponseTo:
        _validate_length(payload.name, 2, 32, "name")
        entity = Tag(id=tag_id, name=payload.name)
        updated = self.repo.update(tag_id, entity)
        return self._to_response(updated)

    def delete_tag(self, tag_id: int) -> None:
        self.repo.delete(tag_id)

    @staticmethod
    def _to_response(entity: Tag) -> dto.TagResponseTo:
        return dto.TagResponseTo(id=entity.id, name=entity.name)


class ArticleService:
    def __init__(
        self,
        repo: BaseRepository[Article],
        creator_repo: BaseRepository[Creator],
        tag_repo: BaseRepository[Tag],
    ) -> None:
        self.repo = repo
        self.creator_repo = creator_repo
        self.tag_repo = tag_repo

    def list_articles(self) -> List[dto.ArticleResponseTo]:
        return [self._to_response(a) for a in self.repo.list_all()]

    def get_article(self, article_id: int) -> dto.ArticleResponseTo:
        article = self.repo.get(article_id)
        if not article:
            raise AppError(404, "Article not found", make_error_code(404, 1))
        return self._to_response(article)

    def create_article(self, payload: dto.ArticleRequestTo) -> dto.ArticleResponseTo:
        self._validate_payload(payload)
        creator = self.creator_repo.get(payload.creatorId)
        if not creator:
            raise AppError(404, "Creator not found", make_error_code(404, 2))

        tag_ids = self._validate_tags(payload.tagIds)

        article = Article(
            id=0,
            creator_id=payload.creatorId,
            title=payload.title,
            content=payload.content,
            tag_ids=tag_ids,
        )
        created = self.repo.create(article)
        return self._to_response(created)

    def update_article(self, article_id: int, payload: dto.ArticleRequestTo) -> dto.ArticleResponseTo:
        self._validate_payload(payload)
        if not self.creator_repo.get(payload.creatorId):
            raise AppError(404, "Creator not found", make_error_code(404, 2))
        tag_ids = self._validate_tags(payload.tagIds)

        existing = self.repo.get(article_id)
        if not existing:
            raise AppError(404, "Article not found", make_error_code(404, 1))

        updated = Article(
            id=article_id,
            creator_id=payload.creatorId,
            title=payload.title,
            content=payload.content,
            created=existing.created,
            modified=datetime.now(timezone.utc).isoformat(),
            tag_ids=tag_ids,
        )
        saved = self.repo.update(article_id, updated)
        return self._to_response(saved)

    def delete_article(self, article_id: int) -> None:
        self.repo.delete(article_id)

    def get_creator_by_article(self, article_id: int) -> dto.CreatorResponseTo:
        article = self.repo.get(article_id)
        if not article:
            raise AppError(404, "Article not found", make_error_code(404, 1))
        creator = self.creator_repo.get(article.creator_id)
        if not creator:
            raise AppError(404, "Creator not found", make_error_code(404, 2))
        return CreatorService._to_response(creator)

    def get_tags_by_article(self, article_id: int) -> List[dto.TagResponseTo]:
        article = self.repo.get(article_id)
        if not article:
            raise AppError(404, "Article not found", make_error_code(404, 1))
        tags = [self.tag_repo.get(tag_id) for tag_id in article.tag_ids]
        result: List[dto.TagResponseTo] = []
        for tag in tags:
            if tag:
                result.append(TagService._to_response(tag))
        return result

    def search_articles(
        self,
        tag_names: Optional[List[str]] = None,
        tag_ids: Optional[List[int]] = None,
        creator_login: Optional[str] = None,
        title: Optional[str] = None,
        content: Optional[str] = None,
    ) -> List[dto.ArticleResponseTo]:
        articles = self.repo.list_all()
        filtered: List[Article] = []
        for article in articles:
            if creator_login:
                creator = self.creator_repo.get(article.creator_id)
                if not creator or creator.login != creator_login:
                    continue
            if title and title not in article.title:
                continue
            if content and content not in article.content:
                continue
            if tag_ids:
                if not all(tag_id in article.tag_ids for tag_id in tag_ids):
                    continue
            if tag_names:
                names = []
                for tid in article.tag_ids:
                    tag = self.tag_repo.get(tid)
                    if tag:
                        names.append(tag.name)
                if not all(name in names for name in tag_names):
                    continue
            filtered.append(article)
        return [self._to_response(a) for a in filtered]

    def _validate_payload(self, payload: dto.ArticleRequestTo) -> None:
        _validate_length(payload.title, 2, 64, "title")
        _validate_length(payload.content, 4, 2048, "content")

    def _validate_tags(self, tag_ids: Optional[List[int]]) -> List[int]:
        if not tag_ids:
            return []
        unique_ids = list(dict.fromkeys(tag_ids))
        for tag_id in unique_ids:
            if not self.tag_repo.get(tag_id):
                raise AppError(404, f"Tag {tag_id} not found", make_error_code(404, 3))
        return unique_ids

    @staticmethod
    def _to_response(entity: Article) -> dto.ArticleResponseTo:
        return dto.ArticleResponseTo(
            id=entity.id,
            creatorId=entity.creator_id,
            title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
            tagIds=list(entity.tag_ids),
        )


class MessageService:
    def __init__(self, repo: BaseRepository[Message], article_repo: BaseRepository[Article]) -> None:
        self.repo = repo
        self.article_repo = article_repo

    def list_messages(self) -> List[dto.MessageResponseTo]:
        return [self._to_response(m) for m in self.repo.list_all()]

    def get_message(self, message_id: int) -> dto.MessageResponseTo:
        message = self.repo.get(message_id)
        if not message:
            raise AppError(404, "Message not found", make_error_code(404, 1))
        return self._to_response(message)

    def create_message(self, payload: dto.MessageRequestTo) -> dto.MessageResponseTo:
        self._validate_payload(payload)
        if not self.article_repo.get(payload.articleId):
            raise AppError(404, "Article not found", make_error_code(404, 2))
        entity = Message(id=0, article_id=payload.articleId, content=payload.content)
        created = self.repo.create(entity)
        return self._to_response(created)

    def update_message(self, message_id: int, payload: dto.MessageRequestTo) -> dto.MessageResponseTo:
        self._validate_payload(payload)
        if not self.article_repo.get(payload.articleId):
            raise AppError(404, "Article not found", make_error_code(404, 2))
        message = Message(id=message_id, article_id=payload.articleId, content=payload.content)
        updated = self.repo.update(message_id, message)
        return self._to_response(updated)

    def delete_message(self, message_id: int) -> None:
        self.repo.delete(message_id)

    def get_by_article(self, article_id: int) -> List[dto.MessageResponseTo]:
        if not self.article_repo.get(article_id):
            raise AppError(404, "Article not found", make_error_code(404, 2))
        return [self._to_response(m) for m in self.repo.list_all() if m.article_id == article_id]

    def _validate_payload(self, payload: dto.MessageRequestTo) -> None:
        _validate_length(payload.content, 2, 2048, "content")

    @staticmethod
    def _to_response(entity: Message) -> dto.MessageResponseTo:
        return dto.MessageResponseTo(id=entity.id, articleId=entity.article_id, content=entity.content)
