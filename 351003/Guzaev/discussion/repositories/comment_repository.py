from cassandra.query import SimpleStatement
from database import session
from models.comment import Comment
import uuid

_counter = 1

class CommentRepository:
    def create(self, comment: Comment) -> Comment:
        global _counter
        comment.id = _counter
        _counter += 1
        session.execute(
            "INSERT INTO tbl_comment (tweet_id, country, id, content) VALUES (%s, %s, %s, %s)",
            (comment.tweet_id, comment.country, comment.id, comment.content)
        )
        return comment

    def get_all(self):
        rows = session.execute("SELECT * FROM tbl_comment")
        return [Comment(id=r.id, tweet_id=r.tweet_id, country=r.country, content=r.content) for r in rows]

    def get_by_id(self, comment_id):
        try:
            cid = int(comment_id)
        except (ValueError, TypeError):
            return None
        rows = session.execute(
            "SELECT * FROM tbl_comment WHERE id=%s ALLOW FILTERING", (cid,)
        )
        for row in rows:
            return Comment(id=row.id, tweet_id=row.tweet_id,
                           country=row.country, content=row.content)
        return None

    def delete(self, comment_id) -> bool:
        try:
            cid = int(comment_id)
        except (ValueError, TypeError):
            return False
        existing = self.get_by_id(cid)
        if not existing:
            return False
        session.execute(
            "DELETE FROM tbl_comment WHERE tweet_id=%s AND id=%s",
            (existing.tweet_id, existing.id)
        )
        return True

    def update(self, comment: Comment) -> Comment:
        session.execute(
            "INSERT INTO tbl_comment (tweet_id, country, id, content) VALUES (%s, %s, %s, %s)",
            (comment.tweet_id, comment.country, comment.id, comment.content)
        )
        return comment

