import time
import os

from fastapi import APIRouter, HTTPException

from dto import CommentRequestTo, CommentResponseTo
from database import get_session

router = APIRouter(
    prefix="/api/v1.0/comments",
    tags=["comments"],
)


def _one_or_none(result_set):
    rows = list(result_set)
    return rows[0] if rows else None


def _generate_id() -> int:
    ts = int(time.time() * 1_000_000)
    pid = os.getpid() & 0xFFFF
    return ((ts & 0xFFFFFFFFFFFF) << 16) | pid


@router.get("", response_model=list[CommentResponseTo])
def get_comments():
    s = get_session()
    rows = s.execute("SELECT id, tweetId, content, country, state FROM tbl_comment_by_id")
    return [
        CommentResponseTo(
            id=r.id, tweetId=r.tweetid, content=r.content,
            country=r.country or "Unknown", state=r.state or "PENDING"
        )
        for r in rows
    ]


@router.get("/{comment_id}", response_model=CommentResponseTo)
def get_comment(comment_id: int):
    s = get_session()
    row = _one_or_none(s.execute(
        "SELECT id, tweetId, content, country, state FROM tbl_comment_by_id WHERE id = %s",
        (comment_id,)
    ))
    if not row:
        raise HTTPException(status_code=404, detail="Comment not found")
    return CommentResponseTo(
        id=row.id, tweetId=row.tweetid, content=row.content,
        country=row.country or "Unknown", state=row.state or "PENDING"
    )


@router.post("", response_model=CommentResponseTo, status_code=201)
def create_comment(data: CommentRequestTo):
    s = get_session()
    comment_id = _generate_id()
    country = data.country or "Unknown"
    state = "PENDING"

    s.execute(
        """
        BEGIN BATCH
          INSERT INTO tbl_comment (tweetId, id, content, country, state) VALUES (%s, %s, %s, %s, %s);
          INSERT INTO tbl_comment_by_id (id, tweetId, content, country, state) VALUES (%s, %s, %s, %s, %s);
        APPLY BATCH
        """,
        (data.tweetId, comment_id, data.content, country, state,
         comment_id, data.tweetId, data.content, country, state)
    )

    return CommentResponseTo(id=comment_id, tweetId=data.tweetId, content=data.content, country=country, state=state)


@router.put("/{comment_id}", response_model=CommentResponseTo)
def update_comment(comment_id: int, data: CommentRequestTo):
    s = get_session()

    old = _one_or_none(s.execute(
        "SELECT id, tweetId, content, country, state FROM tbl_comment_by_id WHERE id = %s",
        (comment_id,)
    ))
    if not old:
        raise HTTPException(status_code=404, detail="Comment not found")

    old_tweet_id = old.tweetid
    new_tweet_id = data.tweetId
    country = data.country or old.country or "Unknown"
    state = old.state or "PENDING"

    if old_tweet_id != new_tweet_id:
        s.execute(
            "DELETE FROM tbl_comment WHERE tweetId = %s AND id = %s",
            (old_tweet_id, comment_id)
        )

    s.execute(
        """
        BEGIN BATCH
          INSERT INTO tbl_comment (tweetId, id, content, country, state) VALUES (%s, %s, %s, %s, %s);
          INSERT INTO tbl_comment_by_id (id, tweetId, content, country, state) VALUES (%s, %s, %s, %s, %s);
        APPLY BATCH
        """,
        (new_tweet_id, comment_id, data.content, country, state,
         comment_id, new_tweet_id, data.content, country, state)
    )

    return CommentResponseTo(id=comment_id, tweetId=new_tweet_id, content=data.content, country=country, state=state)


@router.delete("/{comment_id}", status_code=204)
def delete_comment(comment_id: int):
    s = get_session()

    old = _one_or_none(s.execute(
        "SELECT id, tweetId FROM tbl_comment_by_id WHERE id = %s",
        (comment_id,)
    ))
    if not old:
        raise HTTPException(status_code=404, detail="Comment not found")

    s.execute(
        """
        BEGIN BATCH
          DELETE FROM tbl_comment WHERE tweetId = %s AND id = %s;
          DELETE FROM tbl_comment_by_id WHERE id = %s;
        APPLY BATCH
        """,
        (old.tweetid, comment_id, comment_id)
    )
