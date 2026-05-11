from datetime import datetime, timezone
from models import Author, Issue, Tag, Comment
from schemas import *
from repositories import author_repo, issue_repo, tag_repo, comment_repo
from exceptions import AppError

class AuthorService:
    def create(self, dto: AuthorRequestTo) -> AuthorResponseTo:
        for a in author_repo.find_all():
            if a.login == dto.login:
                raise AppError(403, 40301, "Author with this login already exists")
        entity = Author(login=dto.login, password=dto.password, firstname=dto.firstname, lastname=dto.lastname)
        saved = author_repo.save(entity)
        return AuthorResponseTo(**saved.__dict__)

    def get_all(self) -> list[AuthorResponseTo]:
        return [AuthorResponseTo(**a.__dict__) for a in author_repo.find_all()]

    def get_by_id(self, id: int) -> AuthorResponseTo:
        entity = author_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40401, f"Author with id {id} not found")
        return AuthorResponseTo(**entity.__dict__)

    def update(self, id: int, dto: AuthorRequestTo) -> AuthorResponseTo:
        entity = author_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40401, f"Author with id {id} not found")
        entity.login = dto.login
        entity.password = dto.password
        entity.firstname = dto.firstname
        entity.lastname = dto.lastname
        updated = author_repo.update(entity)
        return AuthorResponseTo(**updated.__dict__)

    def delete(self, id: int):
        if not author_repo.delete(id):
            raise AppError(404, 40401, f"Author with id {id} not found")

class IssueService:
    def create(self, dto: IssueRequestTo) -> IssueResponseTo:
        if not author_repo.find_by_id(dto.authorId):
            raise AppError(400, 40003, f"Author with id {dto.authorId} does not exist")
        entity = Issue(authorId=dto.authorId, title=dto.title, content=dto.content, tagIds=dto.tagIds)
        saved = issue_repo.save(entity)
        return IssueResponseTo(**saved.__dict__)

    def get_all(self) -> list[IssueResponseTo]:
        return [IssueResponseTo(**i.__dict__) for i in issue_repo.find_all()]

    def get_by_id(self, id: int) -> IssueResponseTo:
        entity = issue_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40402, f"Issue with id {id} not found")
        return IssueResponseTo(**entity.__dict__)

    def update(self, id: int, dto: IssueRequestTo) -> IssueResponseTo:
        entity = issue_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40402, f"Issue with id {id} not found")
        if not author_repo.find_by_id(dto.authorId):
            raise AppError(400, 40003, f"Author with id {dto.authorId} does not exist")
        
        entity.authorId = dto.authorId
        entity.title = dto.title
        entity.content = dto.content
        entity.tagIds = dto.tagIds
        entity.modified = datetime.now(timezone.utc)
        updated = issue_repo.update(entity)
        return IssueResponseTo(**updated.__dict__)

    def delete(self, id: int):
        if not issue_repo.delete(id):
            raise AppError(404, 40402, f"Issue with id {id} not found")

class TagService:
    def create(self, dto: TagRequestTo) -> TagResponseTo:
        for t in tag_repo.find_all():
            if t.name == dto.name:
                raise AppError(403, 40302, "Tag with this name already exists")
        entity = Tag(name=dto.name)
        saved = tag_repo.save(entity)
        return TagResponseTo(**saved.__dict__)

    def get_all(self) -> list[TagResponseTo]:
        return [TagResponseTo(**t.__dict__) for t in tag_repo.find_all()]

    def get_by_id(self, id: int) -> TagResponseTo:
        entity = tag_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40403, f"Tag with id {id} not found")
        return TagResponseTo(**entity.__dict__)

    def update(self, id: int, dto: TagRequestTo) -> TagResponseTo:
        entity = tag_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40403, f"Tag with id {id} not found")
        entity.name = dto.name
        updated = tag_repo.update(entity)
        return TagResponseTo(**updated.__dict__)

    def delete(self, id: int):
        if not tag_repo.delete(id):
            raise AppError(404, 40403, f"Tag with id {id} not found")

class CommentService:
    def create(self, dto: CommentRequestTo) -> CommentResponseTo:
        if not issue_repo.find_by_id(dto.issueId):
            raise AppError(400, 40004, f"Issue with id {dto.issueId} does not exist")
        entity = Comment(issueId=dto.issueId, content=dto.content)
        saved = comment_repo.save(entity)
        return CommentResponseTo(**saved.__dict__)

    def get_all(self) -> list[CommentResponseTo]:
        return [CommentResponseTo(**c.__dict__) for c in comment_repo.find_all()]

    def get_by_id(self, id: int) -> CommentResponseTo:
        entity = comment_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40404, f"Comment with id {id} not found")
        return CommentResponseTo(**entity.__dict__)

    def update(self, id: int, dto: CommentRequestTo) -> CommentResponseTo:
        entity = comment_repo.find_by_id(id)
        if not entity:
            raise AppError(404, 40404, f"Comment with id {id} not found")
        if not issue_repo.find_by_id(dto.issueId):
            raise AppError(400, 40004, f"Issue with id {dto.issueId} does not exist")
        entity.issueId = dto.issueId
        entity.content = dto.content
        updated = comment_repo.update(entity)
        return CommentResponseTo(**updated.__dict__)

    def delete(self, id: int):
        if not comment_repo.delete(id):
            raise AppError(404, 40404, f"Comment with id {id} not found")