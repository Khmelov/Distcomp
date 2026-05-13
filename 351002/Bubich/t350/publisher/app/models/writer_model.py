from app.models.database import db, BaseModel


class WriterModel(BaseModel):
    __tablename__ = 'tbl_writer'

    login = db.Column(db.Text, nullable=False, unique=True)
    password = db.Column(db.Text, nullable=False)
    firstname = db.Column(db.Text, nullable=False)
    lastname = db.Column(db.Text, nullable=False)

    stories = db.relationship('StoryModel', backref='writer', lazy='dynamic', cascade='all, delete-orphan')

    def to_dict(self):
        return {
            'id': self.id,
            'login': self.login,
            'firstname': self.firstname,
            'lastname': self.lastname
        }