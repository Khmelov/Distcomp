from app.models.database import db, BaseModel
from datetime import datetime


class StoryModel(BaseModel):
    __tablename__ = 'tbl_story'

    writer_id = db.Column(db.BigInteger, db.ForeignKey('tbl_writer.id', ondelete='CASCADE'), nullable=False)
    title = db.Column(db.Text, nullable=False)
    content = db.Column(db.Text, nullable=False)
    created = db.Column(db.DateTime, default=datetime.utcnow)
    modified = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    __table_args__ = (
        db.UniqueConstraint('writer_id', 'title', name='uq_writer_title'),
    )

    # НЕ ДЕЛАЕМ связь с CommentModel! Comment теперь в Discussion
    # comments = db.relationship('CommentModel', ...)  ← Закомментировано!

    def to_dict(self):
        return {
            'id': self.id,
            'writerId': self.writer_id,
            'title': self.title,
            'content': self.content,
            'created': self.created.isoformat() if self.created else None,
            'modified': self.modified.isoformat() if self.modified else None
        }