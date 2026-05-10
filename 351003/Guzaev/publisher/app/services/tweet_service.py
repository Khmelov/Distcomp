from repositories.base_repository import BaseRepository
from models.models import Tweet, Writer, Marker
from dtos.tweet_dto import TweetRequestTo, TweetResponseTo
from database import SessionLocal
from errors import AppError
from typing import List
from datetime import datetime
from sqlalchemy import func

tweet_repo = BaseRepository[Tweet](Tweet)

class TweetService:
    def create(self, dto: TweetRequestTo) -> TweetResponseTo:
        with SessionLocal() as db:

            writer = db.query(Writer).filter(Writer.id == dto.writer_id).first()
            if not writer:
                raise AppError(status_code=403, message="Writer not found", error_code=40303)

            existing = db.query(Tweet).filter(Tweet.writer_id == dto.writer_id, Tweet.title == dto.title).first()
            if existing:
                raise AppError(status_code=403, message="Tweet title already exists", error_code=40307)

            db_markers: List[Marker] = []


            if dto.marker_ids:
                db_markers = db.query(Marker).filter(Marker.id.in_(dto.marker_ids)).all()
                if len(db_markers) != len(dto.marker_ids):
                    raise AppError(status_code=403, message="Marker not found", error_code=40304)

            if dto.markers:
                for m_name in dto.markers:

                    marker_entity = db.query(Marker).filter(Marker.name == m_name).first()

                    if not marker_entity:
                        marker_entity = Marker(name=m_name)
                        db.add(marker_entity)
                        db.commit()  # Сохраняем, чтобы получить id
                        db.refresh(marker_entity)

                    if marker_entity not in db_markers:
                        db_markers.append(marker_entity)


            entity = Tweet(
                writer_id=dto.writer_id,
                title=dto.title,
                content=dto.content,
                markers=db_markers,
                created=datetime.utcnow(),
                modified=datetime.utcnow(),
            )
            saved = tweet_repo.create(db, entity)

            return TweetResponseTo(
                id=saved.id,
                writerId=saved.writer_id,
                title=saved.title,
                content=saved.content,
                created=saved.created,
                modified=saved.modified,
                markerIds=saved.marker_ids,
            )


    def get_all(self) -> List[TweetResponseTo]:
        with SessionLocal() as db:
            entities = tweet_repo.get_all(db)
            return [
                TweetResponseTo(
                    id=e.id, writerId=e.writer_id, title=e.title,
                    content=e.content, created=e.created,
                    modified=e.modified, markerIds=e.marker_ids
                ) for e in entities
            ]

    def get_by_id(self, id: int) -> TweetResponseTo:
        with SessionLocal() as db:
            entity = tweet_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Tweet not found", error_code=40404)

            return TweetResponseTo(
                id=entity.id, writerId=entity.writer_id, title=entity.title,
                content=entity.content, created=entity.created,
                modified=entity.modified, markerIds=entity.marker_ids
            )

    def update(self, id: int, dto: TweetRequestTo) -> TweetResponseTo:
        with SessionLocal() as db:
            entity = tweet_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Tweet not found", error_code=40405)

            writer = db.query(Writer).filter(Writer.id == dto.writer_id).first()
            if not writer:
                raise AppError(status_code=403, message="Writer not found", error_code=40305)

            db_markers = []
            if dto.marker_ids:
                for m_id in dto.marker_ids:
                    if not db.query(Marker).filter(Marker.id == m_id).first():
                        raise AppError(status_code=403, message=f"Marker {m_id} not found", error_code=40306)
                db_markers = db.query(Marker).filter(Marker.id.in_(dto.marker_ids)).all()

            entity.writer_id = dto.writer_id
            entity.title = dto.title
            entity.content = dto.content
            entity.markers = db_markers
            entity.modified = datetime.utcnow()

            db.commit()
            db.refresh(entity)

            return TweetResponseTo(
                id=entity.id, writerId=entity.writer_id, title=entity.title,
                content=entity.content, created=entity.created,
                modified=entity.modified, markerIds=entity.marker_ids
            )

    def delete(self, id: int) -> None:
        with SessionLocal() as db:
            entity = tweet_repo.get_by_id(db, id)
            if not entity:
                raise AppError(status_code=404, message="Tweet not found", error_code=40406)

            old_markers = list(entity.markers)

            success = tweet_repo.delete(db, id)
            if not success:
                raise AppError(status_code=404, message="Tweet not found", error_code=40406)

            for marker in old_markers:
                cnt = (
                    db.query(func.count(Tweet.id))
                    .join(Tweet.markers)
                    .filter(Marker.id == marker.id)
                    .scalar()
                )
                if cnt == 0:
                    db.delete(marker)

            db.commit()
