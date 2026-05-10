from fastapi import Depends
from sqlalchemy.orm import Session
from db.database import SessionLocal, create_tables
from repository.sqlalchemy_repository import SQLAlchemyRepository
from models.db_models import TblUser, TblTopic, TblMarker, TblNotice, TblTopicMarker
from models.user import User
from models.topic import Topic
from models.marker import Marker
from models.notice import Notice
from services.user import UserService
from services.topic import TopicService
from services.marker import MarkerService
from services.notice import NoticeService
from typing import Optional


def init_db():
    create_tables()


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


class UserRepository(SQLAlchemyRepository[User, TblUser]):
    def __init__(self, db: Session):
        super().__init__(db, TblUser)

    def _to_entity(self, db_obj: TblUser) -> User:
        return User(
            id=db_obj.id,
            login=db_obj.login,
            password=db_obj.password,
            firstname=db_obj.firstname,
            lastname=db_obj.lastname,
        )

    def _to_model(self, entity: User) -> TblUser:
        return TblUser(
            id=entity.id,
            login=entity.login,
            password=entity.password,
            firstname=entity.firstname,
            lastname=entity.lastname,
        )


class TopicRepository(SQLAlchemyRepository[Topic, TblTopic]):
    _immutable_fields = {"id", "created", "modified"}

    def __init__(self, db: Session):
        super().__init__(db, TblTopic)

    def _to_entity(self, db_obj: TblTopic) -> Topic:
        marker_ids = (
            [tm.marker_id for tm in db_obj.topic_markers]
            if db_obj.topic_markers
            else []
        )
        return Topic(
            id=db_obj.id,
            userId=db_obj.user_id,
            title=db_obj.title,
            content=db_obj.content,
            markerIds=marker_ids,
            created=db_obj.created,
            modified=db_obj.modified,
        )

    def _to_model(self, entity: Topic) -> TblTopic:
        return TblTopic(
            id=entity.id,
            user_id=entity.userId,
            title=entity.title,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
        )

    def _sync_markers(self, db_topic: TblTopic, marker_ids: list[int]):
        """Обновляет связи в tbl_topic_marker."""
        # Удаляем старые связи
        db_topic.topic_markers.clear()
        # Добавляем новые
        for mid in marker_ids:
            db_topic.topic_markers.append(TblTopicMarker(marker_id=mid))
        self._session.flush()

    def create(self, entity: Topic) -> Topic:
        db_obj = self._to_model(entity)
        self._session.add(db_obj)
        self._session.flush()
        self._sync_markers(db_obj, entity.markerIds)
        self._session.commit()
        self._session.refresh(db_obj)
        return self._to_entity(db_obj)

    def update(self, entity: Topic) -> Topic:
        db_obj = self._session.query(TblTopic).filter(TblTopic.id == entity.id).first()
        if not db_obj:
            raise ValueError(f"Entity with id {entity.id} not found")
        for key, value in self._entity_to_dict(entity).items():
            if key not in self._immutable_fields:
                setattr(db_obj, key, value)
        self._sync_markers(db_obj, entity.markerIds)
        self._session.commit()
        self._session.refresh(db_obj)
        return self._to_entity(db_obj)


class MarkerRepository(SQLAlchemyRepository[Marker, TblMarker]):
    def __init__(self, db: Session):
        super().__init__(db, TblMarker)

    def _to_entity(self, db_obj: TblMarker) -> Marker:
        return Marker(id=db_obj.id, name=db_obj.name)

    def _to_model(self, entity: Marker) -> TblMarker:
        return TblMarker(id=entity.id, name=entity.name)

    def get_by_name(self, name: str) -> Optional[Marker]:
        db_obj = self._session.query(TblMarker).filter(TblMarker.name == name).first()
        return self._to_entity(db_obj) if db_obj else None

    def create_by_name(self, name: str) -> Marker:
        return self.create(Marker(name=name))


class NoticeRepository(SQLAlchemyRepository[Notice, TblNotice]):
    _immutable_fields = {"id", "created", "modified"}

    def __init__(self, db: Session):
        super().__init__(db, TblNotice)

    def _to_entity(self, db_obj: TblNotice) -> Notice:
        return Notice(
            id=db_obj.id,
            topicId=db_obj.topic_id,
            content=db_obj.content,
            created=db_obj.created,
            modified=db_obj.modified,
        )

    def _to_model(self, entity: Notice) -> TblNotice:
        return TblNotice(
            id=entity.id,
            topic_id=entity.topicId,
            content=entity.content,
            created=entity.created,
            modified=entity.modified,
        )


def get_user_service(db: Session = Depends(get_db)) -> UserService:
    return UserService(UserRepository(db))


def get_topic_service(db: Session = Depends(get_db)) -> TopicService:
    return TopicService(TopicRepository(db), UserRepository(db), MarkerRepository(db))


def get_marker_service(db: Session = Depends(get_db)) -> MarkerService:
    return MarkerService(MarkerRepository(db))


def get_notice_service(db: Session = Depends(get_db)) -> NoticeService:
    return NoticeService(NoticeRepository(db), TopicRepository(db))
