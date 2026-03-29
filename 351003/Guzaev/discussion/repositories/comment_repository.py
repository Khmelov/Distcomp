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

    def get_by_id(self, comment_id: int):
        rows = session.execute("SELECT * FROM tbl_comment WHERE id=%s ALLOW FILTERING", (comment_id,))
        for row in rows:
            return Comment(id=row.id, tweet_id=row.tweet_id, country=row.country, content=row.content)
        return None

    def update(self, comment: Comment) -> Comment:
        session.execute(
            "INSERT INTO tbl_comment (tweet_id, country, id, content) VALUES (%s, %s, %s, %s)",
            (comment.tweet_id, comment.country, comment.id, comment.content)
        )
        return comment

    def delete(self, comment_id: int) -> bool:
        existing = self.get_by_id(comment_id)
        if not existing:
            return False
        session.execute(
            "DELETE FROM tbl_comment WHERE tweet_id=%s AND country=%s AND id=%s",
            (existing.tweet_id, existing.country, existing.id)
        )
        return True