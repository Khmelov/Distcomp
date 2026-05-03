from sqlalchemy.orm import Session
from database import SessionLocal
from models.models import Writer

class WriterRepository:
    def __init__(self):
        self.db: Session = SessionLocal()

    def create(self, login: str, password: str, firstname: str, lastname: str, role: str = "CUSTOMER") -> Writer:
        writer = Writer(
            login=login,
            password=password,
            firstname=firstname,
            lastname=lastname,
            role=role
        )
        self.db.add(writer)
        self.db.commit()
        self.db.refresh(writer)
        return writer

    def get_all(self):
        return self.db.query(Writer).all()

    def get_by_id(self, writer_id: int):
        return self.db.query(Writer).filter(Writer.id == writer_id).first()

    def get_by_login(self, login: str):
        return self.db.query(Writer).filter(Writer.login == login).first()

    def update(self, writer_id: int, **kwargs) -> Writer:
        writer = self.get_by_id(writer_id)
        if not writer:
            return None
        for key, value in kwargs.items():
            if hasattr(writer, key) and value is not None:
                setattr(writer, key, value)
        self.db.commit()
        self.db.refresh(writer)
        return writer

    def delete(self, writer_id: int) -> bool:
        writer = self.get_by_id(writer_id)
        if not writer:
            return False
        self.db.delete(writer)
        self.db.commit()
        return True