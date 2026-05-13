from app.models.database import db, BaseModel


class MarkModel(BaseModel):
    __tablename__ = 'tbl_mark'

    name = db.Column(db.Text, nullable=False)

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name
        }