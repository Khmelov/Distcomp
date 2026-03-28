import time
from typing import List, Optional
from app.database import get_collection
from app.schemas.comment import CommentCreate, CommentUpdate, CommentResponse


def _next_id() -> int:
    return int(time.time() * 1000)


def get_all() -> List[CommentResponse]:
    col = get_collection()
    return [CommentResponse(id=r["id"], issue_id=r["issue_id"], content=r["content"]) for r in col.find()]


def get_by_id(comment_id: int) -> Optional[CommentResponse]:
    col = get_collection()
    r = col.find_one({"id": comment_id})
    if not r:
        return None
    return CommentResponse(id=r["id"], issue_id=r["issue_id"], content=r["content"])


def create(data: CommentCreate) -> CommentResponse:
    col = get_collection()
    comment_id = _next_id()
    col.insert_one({"id": comment_id, "issue_id": data.issue_id, "content": data.content})
    return CommentResponse(id=comment_id, issue_id=data.issue_id, content=data.content)


def update(comment_id: int, data: CommentUpdate) -> Optional[CommentResponse]:
    col = get_collection()
    result = col.find_one_and_update(
        {"id": comment_id},
        {"$set": {"issue_id": data.issue_id, "content": data.content}},
        return_document=True
    )
    if not result:
        return None
    return CommentResponse(id=comment_id, issue_id=data.issue_id, content=data.content)


def delete(comment_id: int) -> bool:
    col = get_collection()
    result = col.delete_one({"id": comment_id})
    return result.deleted_count > 0