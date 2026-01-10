from __future__ import annotations

from typing import List, Optional

from sqlalchemy import select
from sqlalchemy.orm import Session

from app import schemas
from app.exceptions import AppError, error_code
from app.models import Article, Creator, Message, Tag
from app.validation import check_length


def _paginate(query, page: int, size: int):
    return query.offset(page * size).limit(size)


# Creator
def list_creators(db: Session, login_filter: Optional[str], page: int, size: int):
    stmt = select(Creator)
    if login_filter:
        stmt = stmt.where(Creator.login.ilike(f"%{login_filter}%"))
    stmt = _paginate(stmt.order_by(Creator.id), page, size)
    return [to_creator_dto(c) for c in db.scalars(stmt).all()]


def get_creator(db: Session, creator_id: int) -> schemas.CreatorResponse:
    creator = db.get(Creator, creator_id)
    if not creator:
        raise AppError(404, "Creator not found", error_code(404, 1))
    return to_creator_dto(creator)


def create_creator(db: Session, req: schemas.CreatorRequest) -> schemas.CreatorResponse:
    _validate_creator(req)
    exists = db.execute(select(Creator.id).where(Creator.login == req.login)).first()
    if exists:
        raise AppError(403, "Creator login already exists", error_code(403, 1))
    creator = Creator(
        login=req.login,
        password=req.password,
        firstname=req.firstname,
        lastname=req.lastname,
    )
    db.add(creator)
    db.flush()
    return to_creator_dto(creator)


def update_creator(db: Session, creator_id: int, req: schemas.CreatorRequest) -> schemas.CreatorResponse:
    _validate_creator(req)
    creator = db.get(Creator, creator_id)
    if not creator:
        raise AppError(404, "Creator not found", error_code(404, 1))
    exists = db.execute(
        select(Creator.id).where(Creator.login == req.login, Creator.id != creator_id)
    ).first()
    if exists:
        raise AppError(403, "Creator login already exists", error_code(403, 1))
    creator.login = req.login
    creator.password = req.password
    creator.firstname = req.firstname
    creator.lastname = req.lastname
    db.add(creator)
    db.flush()
    return to_creator_dto(creator)


def delete_creator(db: Session, creator_id: int) -> None:
    creator = db.get(Creator, creator_id)
    if not creator:
        raise AppError(404, "Creator not found", error_code(404, 1))
    db.delete(creator)


def _validate_creator(req: schemas.CreatorRequest) -> None:
    check_length(req.login, 2, 64, "login")
    check_length(req.password, 8, 128, "password")
    check_length(req.firstname, 2, 64, "firstname")
    check_length(req.lastname, 2, 64, "lastname")


def to_creator_dto(creator: Creator) -> schemas.CreatorResponse:
    return schemas.CreatorResponse(
        id=creator.id,
        login=creator.login,
        password=creator.password,
        firstname=creator.firstname,
        lastname=creator.lastname,
    )


# Tag
def list_tags(db: Session, name_filter: Optional[str], page: int, size: int):
    stmt = select(Tag)
    if name_filter:
        stmt = stmt.where(Tag.name.ilike(f"%{name_filter}%"))
    stmt = _paginate(stmt.order_by(Tag.id.desc()), page, size)
    return [to_tag_dto(t) for t in db.scalars(stmt).all()]


def get_tag(db: Session, tag_id: int) -> schemas.TagResponse:
    tag = db.get(Tag, tag_id)
    if not tag:
        raise AppError(404, "Tag not found", error_code(404, 1))
    return to_tag_dto(tag)


def create_tag(db: Session, req: schemas.TagRequest) -> schemas.TagResponse:
    _validate_tag(req)
    exists = db.execute(select(Tag.id).where(Tag.name == req.name)).first()
    if exists:
        raise AppError(403, "Tag name already exists", error_code(403, 1))
    tag = Tag(name=req.name)
    db.add(tag)
    db.flush()
    return to_tag_dto(tag)


def update_tag(db: Session, tag_id: int, req: schemas.TagRequest) -> schemas.TagResponse:
    _validate_tag(req)
    tag = db.get(Tag, tag_id)
    if not tag:
        raise AppError(404, "Tag not found", error_code(404, 1))
    exists = db.execute(
        select(Tag.id).where(Tag.name == req.name, Tag.id != tag_id)
    ).first()
    if exists:
        raise AppError(403, "Tag name already exists", error_code(403, 1))
    tag.name = req.name
    db.add(tag)
    db.flush()
    return to_tag_dto(tag)


def delete_tag(db: Session, tag_id: int) -> None:
    tag = db.get(Tag, tag_id)
    if not tag:
        raise AppError(404, "Tag not found", error_code(404, 1))
    db.delete(tag)


def _validate_tag(req: schemas.TagRequest) -> None:
    check_length(req.name, 2, 32, "name")


def to_tag_dto(tag: Tag) -> schemas.TagResponse:
    return schemas.TagResponse(id=tag.id, name=tag.name)


# Article
def list_articles(db: Session, creator_id: Optional[int], page: int, size: int):
    stmt = select(Article)
    if creator_id is not None:
        stmt = stmt.where(Article.creator_id == creator_id)
    stmt = _paginate(stmt.order_by(Article.id), page, size)
    return [to_article_dto(a) for a in db.scalars(stmt).all()]


def get_article(db: Session, article_id: int) -> schemas.ArticleResponse:
    article = db.get(Article, article_id)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 1))
    return to_article_dto(article)


def create_article(db: Session, req: schemas.ArticleRequest) -> schemas.ArticleResponse:
    _validate_article(req)
    creator = db.get(Creator, req.creatorId)
    if not creator:
        raise AppError(404, "Creator not found", error_code(404, 2))
    exists = db.execute(select(Article.id).where(Article.title == req.title)).first()
    if exists:
        raise AppError(403, "Article title already exists", error_code(403, 1))
    article = Article(
        creator=creator,
        title=req.title,
        content=req.content,
    )
    article.tags = _resolve_tags(db, req.tagIds, req.tagNames, req.creatorId)
    db.add(article)
    db.flush()
    return to_article_dto(article)


def update_article(db: Session, article_id: int, req: schemas.ArticleRequest) -> schemas.ArticleResponse:
    _validate_article(req)
    article = db.get(Article, article_id)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 1))
    creator = db.get(Creator, req.creatorId)
    if not creator:
        raise AppError(404, "Creator not found", error_code(404, 2))
    exists = db.execute(
        select(Article.id).where(Article.title == req.title, Article.id != article_id)
    ).first()
    if exists:
        raise AppError(403, "Article title already exists", error_code(403, 1))
    article.creator = creator
    article.title = req.title
    article.content = req.content
    article.tags = _resolve_tags(db, req.tagIds, req.tagNames, req.creatorId)
    db.add(article)
    db.flush()
    return to_article_dto(article)


def delete_article(db: Session, article_id: int) -> None:
    article = db.get(Article, article_id)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 1))
    db.delete(article)


def get_article_creator(db: Session, article_id: int) -> schemas.CreatorResponse:
    article = db.get(Article, article_id)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 1))
    return to_creator_dto(article.creator)


def get_article_tags(db: Session, article_id: int) -> List[schemas.TagResponse]:
    article = db.get(Article, article_id)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 1))
    return [to_tag_dto(t) for t in article.tags]


def get_article_messages(db: Session, article_id: int) -> List[schemas.MessageResponse]:
    article = db.get(Article, article_id)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 1))
    return [to_message_dto(m) for m in article.messages]


def _validate_article(req: schemas.ArticleRequest) -> None:
    check_length(req.title, 2, 64, "title")
    check_length(req.content, 4, 2048, "content")


def _resolve_tags(db: Session, ids: Optional[List[int]], names: Optional[List[str]], creator_id: int):
    ids = ids or []
    names = names or []
    if not ids and not names:
        names = [f"red{creator_id}", f"green{creator_id}", f"blue{creator_id}"]
    tags: List[Tag] = []

    if ids:
        tags_by_id = db.scalars(select(Tag).where(Tag.id.in_(ids))).all()
        if len(tags_by_id) != len(set(ids)):
            raise AppError(404, "Tag not found", error_code(404, 3))
        tags.extend(tags_by_id)

    if names:
        existing = {t.name: t for t in db.scalars(select(Tag).where(Tag.name.in_(names))).all()}
        for name in names:
            if name in existing:
                tags.append(existing[name])
            else:
                new_tag = Tag(name=name)
                db.add(new_tag)
                db.flush()
                tags.append(new_tag)

    return tags


def to_article_dto(article: Article) -> schemas.ArticleResponse:
    tag_ids = [t.id for t in article.tags]
    tag_names = [t.name for t in article.tags]
    return schemas.ArticleResponse(
        id=article.id,
        creatorId=article.creator_id,
        title=article.title,
        content=article.content,
        created=article.created,
        modified=article.modified,
        tagIds=tag_ids,
        tagNames=tag_names,
    )


# Message
def list_messages(db: Session, article_id: Optional[int], page: int, size: int):
    stmt = select(Message)
    if article_id is not None:
        stmt = stmt.where(Message.article_id == article_id)
    stmt = _paginate(stmt.order_by(Message.id), page, size)
    return [to_message_dto(m) for m in db.scalars(stmt).all()]


def get_message(db: Session, message_id: int) -> schemas.MessageResponse:
    message = db.get(Message, message_id)
    if not message:
        raise AppError(404, "Message not found", error_code(404, 1))
    return to_message_dto(message)


def create_message(db: Session, req: schemas.MessageRequest) -> schemas.MessageResponse:
    _validate_message(req)
    article = db.get(Article, req.articleId)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 2))
    message = Message(article=article, content=req.content)
    db.add(message)
    db.flush()
    return to_message_dto(message)


def update_message(db: Session, message_id: int, req: schemas.MessageRequest) -> schemas.MessageResponse:
    _validate_message(req)
    message = db.get(Message, message_id)
    if not message:
        raise AppError(404, "Message not found", error_code(404, 1))
    article = db.get(Article, req.articleId)
    if not article:
        raise AppError(404, "Article not found", error_code(404, 2))
    message.article = article
    message.content = req.content
    db.add(message)
    db.flush()
    return to_message_dto(message)


def delete_message(db: Session, message_id: int) -> None:
    message = db.get(Message, message_id)
    if not message:
        raise AppError(404, "Message not found", error_code(404, 1))
    db.delete(message)


def _validate_message(req: schemas.MessageRequest) -> None:
    check_length(req.content, 2, 2048, "content")


def to_message_dto(message: Message) -> schemas.MessageResponse:
    return schemas.MessageResponse(id=message.id, articleId=message.article_id, content=message.content)
