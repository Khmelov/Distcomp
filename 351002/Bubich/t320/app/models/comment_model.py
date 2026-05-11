from app.models.database import db, BaseModel


class CommentModel(BaseModel):
    __tablename__ = 'tbl_comment'

    story_id = db.Column(db.BigInteger, db.ForeignKey('tbl_story.id', ondelete='CASCADE'), nullable=False)
    content = db.Column(db.Text, nullable=False)

    def to_dict(self):
        return {
            'id': self.id,
            'storyId': self.story_id,  # Возвращаем camelCase
            'content': self.content
        }