from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from app.dto.topic import TopicRequestTo, TopicResponseTo
from app.models.topic import Topic
from app.models.mark import Mark
from app.core.exceptions import NotFoundException, AppException
from app.cache.redis_client import cache_get, cache_set, cache_delete


class TopicService:
    def __init__(self, db: Session):
        self.db = db

    def create(self, dto: TopicRequestTo) -> TopicResponseTo:
        topic = Topic(title=dto.title, content=dto.content, user_id=dto.userId)
        if dto.marks:
            for mark_name in dto.marks:
                mark = self.db.query(Mark).filter(
                    Mark.name == mark_name).first()
                if not mark:
                    mark = Mark(name=mark_name)
                    self.db.add(mark)
                topic.marks.append(mark)
        try:
            self.db.add(topic)
            self.db.commit()
            self.db.refresh(topic)
        except IntegrityError as e:
            self.db.rollback()
            err_msg = str(e.orig).lower()
            if "unique constraint" in err_msg or "уникальности" in err_msg:
                raise AppException("Duplicate title", 40302, 403)
            else:
                raise AppException("Invalid association", 40002, 400)
        result = self._to_response(topic)
        cache_set(f"topic:{topic.id}", result.model_dump())
        cache_delete("topics:all")
        return result

    def find_all(self):
        cached = cache_get("topics:all")
        if cached is not None:
            return [TopicResponseTo(**t) for t in cached]
        topics = self.db.query(Topic).all()
        result = [self._to_response(t) for t in topics]
        cache_set("topics:all", [r.model_dump() for r in result])
        return result

    def find_by_id(self, id: int):
        cached = cache_get(f"topic:{id}")
        if cached is not None:
            return TopicResponseTo(**cached)
        topic = self.db.query(Topic).filter(Topic.id == id).first()
        if not topic:
            raise NotFoundException("Topic not found", 40402)
        result = self._to_response(topic)
        cache_set(f"topic:{id}", result.model_dump())
        return result

    def update(self, dto: TopicRequestTo):
        topic = self.db.query(Topic).filter(Topic.id == dto.id).first()
        if not topic:
            raise NotFoundException("Topic not found", 40402)

        topic.title = dto.title
        topic.content = dto.content
        topic.user_id = dto.userId

        old_marks = list(topic.marks)
        topic.marks.clear()

        if dto.marks:
            for mark_name in dto.marks:
                mark = self.db.query(Mark).filter(
                    Mark.name == mark_name).first()
                if not mark:
                    mark = Mark(name=mark_name)
                    self.db.add(mark)
                topic.marks.append(mark)

        try:
            self.db.commit()
            self.db.refresh(topic)

            for m in old_marks:
                mark_in_db = self.db.query(Mark).filter(
                    Mark.id == m.id).first()
                if mark_in_db and len(mark_in_db.topics) == 0:
                    self.db.delete(mark_in_db)
            self.db.commit()
        except IntegrityError:
            self.db.rollback()
            raise AppException("Duplicate title or invalid user", 40002, 400)

        result = self._to_response(topic)
        cache_set(f"topic:{topic.id}", result.model_dump())
        cache_delete("topics:all")
        return result

    def delete(self, id: int):
        topic = self.db.query(Topic).filter(Topic.id == id).first()
        if not topic:
            raise NotFoundException("Topic not found", 40402)

        old_marks = list(topic.marks)
        self.db.delete(topic)
        self.db.commit()

        for m in old_marks:
            mark_in_db = self.db.query(Mark).filter(Mark.id == m.id).first()
            if mark_in_db and len(mark_in_db.topics) == 0:
                self.db.delete(mark_in_db)
        self.db.commit()

        cache_delete(f"topic:{id}")
        cache_delete("topics:all")

    def _to_response(self, topic: Topic) -> TopicResponseTo:
        return TopicResponseTo(
            id=topic.id,
            title=topic.title,
            content=topic.content,
            userId=topic.user_id,
            markIds=[m.id for m in topic.marks]
        )
