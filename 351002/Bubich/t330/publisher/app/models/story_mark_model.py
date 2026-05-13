from app.models.database import db, BaseModel


class StoryMarkModel(BaseModel):
    __tablename__ = 'tbl_story_mark'

    story_id = db.Column(db.BigInteger, db.ForeignKey('tbl_story.id'), nullable=False)
    mark_id = db.Column(db.BigInteger, db.ForeignKey('tbl_mark.id'), nullable=False)

    # Уникальность пары story_id и mark_id
    __table_args__ = (
        db.UniqueConstraint('story_id', 'mark_id', name='uq_story_mark'),
    )