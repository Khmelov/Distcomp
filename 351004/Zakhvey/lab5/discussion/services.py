from .models import CommentModel, CommentByIdModel
from .schemas import CommentRequestTo, CommentResponseTo
from datetime import datetime


class DiscussionService:
    @staticmethod
    def create(dto: CommentRequestTo) -> CommentResponseTo:
        country = (dto.country or "Unknown").strip()
        now = datetime.utcnow()
        new_id = int(now.timestamp() * 1000)
        CommentModel.create(country=country, issue_id=dto.issueId, id=new_id, content=dto.content)
        CommentByIdModel.create(id=new_id, country=country, issue_id=dto.issueId, content=dto.content, created=now, modified=now)
        return CommentResponseTo(id=new_id, issueId=dto.issueId, country=country, content=dto.content)

    @staticmethod
    def get_all():
        return [CommentResponseTo(id=c.id, issueId=c.issue_id, country=c.country, content=c.content) for c in CommentModel.objects.all()]

    @staticmethod
    def get_by_issue(issue_id: int):
        return [CommentResponseTo(id=c.id, issueId=c.issue_id, country=c.country, content=c.content) for c in CommentModel.objects.filter(issue_id=issue_id).all()]

    @staticmethod
    def get_by_id(comment_id: int):
        res = CommentByIdModel.objects.filter(id=comment_id).first()
        if res:
            return CommentResponseTo(id=res.id, issueId=res.issue_id, country=res.country, content=res.content)
        return None

    @staticmethod
    def delete(comment_id: int):
        res = CommentByIdModel.objects.filter(id=comment_id).first()
        if res:
            try:
                CommentModel.objects.filter(issue_id=res.issue_id, country=res.country, id=res.id).delete()
            except Exception:
                pass
            res.delete()
            return True
        return False

    @staticmethod
    def update(dto: CommentRequestTo) -> CommentResponseTo:
        if dto.id is None:
            return None
        res = CommentByIdModel.objects.filter(id=dto.id).first()
        if not res:
            return None
        country = (dto.country or res.country or "Unknown").strip()
        now = datetime.utcnow()
        CommentModel.objects.filter(issue_id=res.issue_id, country=res.country, id=res.id).update(content=dto.content, modified=now)
        res.update(content=dto.content, country=country, modified=now)
        return CommentResponseTo(id=res.id, issueId=res.issue_id, country=country, content=dto.content)
