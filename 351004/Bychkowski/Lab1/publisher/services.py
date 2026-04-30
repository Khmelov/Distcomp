import requests
import time
import json
import redis
from kafka import KafkaProducer
from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from models import Writer, Article, Label
from schemas import *
from repositories import PostgresRepository
from exceptions import AppError
from security import get_password_hash, verify_password, create_access_token

DISCUSSION_URL = "http://localhost:24130/api/v1.0/posts"

kafka_producer = None


def get_producer():
    global kafka_producer
    if kafka_producer is None:
        try:
            kafka_producer = KafkaProducer(
                bootstrap_servers=['localhost:9092'],
                value_serializer=lambda v: json.dumps(v).encode('utf-8'),
                key_serializer=lambda k: str(k).encode('utf-8'),
                linger_ms=0
            )
        except Exception:
            pass
    return kafka_producer


redis_client = None
try:
    redis_client = redis.Redis(host='localhost', port=6379, db=0, decode_responses=True)
    redis_client.ping()
except Exception:
    redis_client = None


def cache_get(key: str):
    if redis_client:
        try:
            return redis_client.get(key)
        except:
            pass
    return None


def cache_set(key: str, value_str: str, ex: int = 300):
    if redis_client:
        try:
            redis_client.set(key, value_str, ex=ex)
        except:
            pass


def cache_delete(*keys):
    if redis_client:
        try:
            redis_client.delete(*keys)
        except:
            pass


def invalidate_articles():
    if redis_client:
        try:
            redis_client.delete("article_all")
            for key in redis_client.scan_iter("article:*"):
                redis_client.delete(key)
        except:
            pass


class BaseService:
    def __init__(self, db: Session):
        self.db = db


class AuthService(BaseService):
    def login(self, dto: LoginRequestTo) -> TokenResponseTo:
        user = self.db.query(Writer).filter(Writer.login == dto.login).first()
        if not user or not verify_password(dto.password, user.password):
            raise AppError(401, 40103, "Invalid login or password")
        token = create_access_token({"sub": user.login, "role": getattr(user, 'role', 'CUSTOMER')})
        return TokenResponseTo(access_token=token)


class WriterService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.repo = PostgresRepository(Writer, db)

    def create(self, dto: WriterRequestTo) -> WriterResponseTo:
        try:
            entity = Writer(login=dto.login, password=get_password_hash(dto.password), firstname=dto.firstname,
                            lastname=dto.lastname, role=getattr(dto, 'role', 'CUSTOMER'))
            saved = self.repo.save(entity)
            resp = WriterResponseTo(id=saved.id, login=saved.login, firstname=saved.firstname, lastname=saved.lastname)
            cache_delete("writer_all")
            return resp
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40301, "Writer with this login already exists")

    def get_all(self) -> list[WriterResponseTo]:
        cached = cache_get("writer_all")
        if cached: return [WriterResponseTo.model_validate(item) for item in json.loads(cached)]
        resp = [WriterResponseTo(id=w.id, login=w.login, firstname=w.firstname, lastname=w.lastname) for w in
                self.repo.find_all()]
        cache_set("writer_all", json.dumps([d.model_dump(mode='json') for d in resp]))
        return resp

    def get_by_id(self, id: int) -> WriterResponseTo:
        cache_key = f"writer:{id}"
        cached = cache_get(cache_key)
        if cached: return WriterResponseTo.model_validate_json(cached)
        entity = self.repo.find_by_id(id)
        if not entity: raise AppError(404, 40401, f"Writer with id {id} not found")
        resp = WriterResponseTo(id=entity.id, login=entity.login, firstname=entity.firstname, lastname=entity.lastname)
        cache_set(cache_key, resp.model_dump_json())
        return resp

    def update(self, id: int, dto: WriterRequestTo) -> WriterResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity: raise AppError(404, 40401, f"Writer with id {id} not found")
        entity.login, entity.password = dto.login, get_password_hash(dto.password)
        entity.firstname, entity.lastname = dto.firstname, dto.lastname
        try:
            updated = self.repo.update(entity)
            resp = WriterResponseTo(id=updated.id, login=updated.login, firstname=updated.firstname,
                                    lastname=updated.lastname)
            cache_set(f"writer:{id}", resp.model_dump_json())
            cache_delete("writer_all")
            return resp
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40301, "Writer with this login already exists")

    def delete(self, id: int):
        writer = self.repo.find_by_id(id)
        if not writer: raise AppError(404, 40401, f"Writer with id {id} not found")
        for article in writer.articles:
            try:
                requests.delete(f"{DISCUSSION_URL}/by-article/{article.id}", timeout=2)
            except Exception:
                pass
        self.repo.delete(id)
        cache_delete(f"writer:{id}", "writer_all")
        invalidate_articles()


class LabelService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.repo = PostgresRepository(Label, db)

    def create(self, dto: LabelRequestTo) -> LabelResponseTo:
        try:
            entity = Label(name=dto.name)
            saved = self.repo.save(entity)
            resp = LabelResponseTo(id=saved.id, name=saved.name)
            cache_delete("label_all")
            return resp
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40302, "Label with this name already exists")

    def get_all(self) -> list[LabelResponseTo]:
        cached = cache_get("label_all")
        if cached: return [LabelResponseTo.model_validate(item) for item in json.loads(cached)]
        resp = [LabelResponseTo(id=l.id, name=l.name) for l in self.repo.find_all()]
        cache_set("label_all", json.dumps([d.model_dump(mode='json') for d in resp]))
        return resp

    def get_by_id(self, id: int) -> LabelResponseTo:
        cache_key = f"label:{id}"
        cached = cache_get(cache_key)
        if cached: return LabelResponseTo.model_validate_json(cached)
        entity = self.repo.find_by_id(id)
        if not entity: raise AppError(404, 40403, f"Label with id {id} not found")
        resp = LabelResponseTo(id=entity.id, name=entity.name)
        cache_set(cache_key, resp.model_dump_json())
        return resp

    def update(self, id: int, dto: LabelRequestTo) -> LabelResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity: raise AppError(404, 40403, f"Label with id {id} not found")
        entity.name = dto.name
        try:
            updated = self.repo.update(entity)
            resp = LabelResponseTo(id=updated.id, name=updated.name)
            cache_set(f"label:{id}", resp.model_dump_json())
            cache_delete("label_all")
            invalidate_articles()
            return resp
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40302, "Label with this name already exists")

    def delete(self, id: int):
        if not self.repo.delete(id): raise AppError(404, 40403, f"Label with id {id} not found")
        cache_delete(f"label:{id}", "label_all")
        invalidate_articles()


class ArticleService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.repo = PostgresRepository(Article, db)
        self.writer_repo = PostgresRepository(Writer, db)
        self.label_repo = PostgresRepository(Label, db)

    def create(self, dto: ArticleRequestTo) -> ArticleResponseTo:
        if not self.writer_repo.find_by_id(dto.writerId): raise AppError(400, 40003,
                                                                         f"Writer with id {dto.writerId} does not exist")
        labels = []
        if dto.labelIds:
            for lid in dto.labelIds:
                lbl = self.label_repo.find_by_id(lid)
                if not lbl: raise AppError(400, 40005, f"Label with id {lid} not found")
                labels.append(lbl)
        try:
            entity = Article(writer_id=dto.writerId, title=dto.title, content=dto.content)
            entity.labels = labels
            saved = self.repo.save(entity)
            resp = self._map_to_dto(saved)
            cache_delete("article_all")
            return resp
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40303, "Article with this title already exists")

    def get_all(self) -> list[ArticleResponseTo]:
        cached = cache_get("article_all")
        if cached: return [ArticleResponseTo.model_validate(item) for item in json.loads(cached)]
        resp = [self._map_to_dto(a) for a in self.repo.find_all()]
        cache_set("article_all", json.dumps([d.model_dump(mode='json') for d in resp]))
        return resp

    def get_by_id(self, id: int) -> ArticleResponseTo:
        cache_key = f"article:{id}"
        cached = cache_get(cache_key)
        if cached: return ArticleResponseTo.model_validate_json(cached)
        entity = self.repo.find_by_id(id)
        if not entity: raise AppError(404, 40402, f"Article with id {id} not found")
        resp = self._map_to_dto(entity)
        cache_set(cache_key, resp.model_dump_json())
        return resp

    def update(self, id: int, dto: ArticleRequestTo) -> ArticleResponseTo:
        entity = self.repo.find_by_id(id)
        if not entity: raise AppError(404, 40402, f"Article with id {id} not found")
        if not self.writer_repo.find_by_id(dto.writerId): raise AppError(400, 40003,
                                                                         f"Writer with id {dto.writerId} does not exist")
        entity.writer_id, entity.title, entity.content = dto.writerId, dto.title, dto.content
        if dto.labelIds is not None:
            new_labels = []
            for lid in dto.labelIds:
                l = self.label_repo.find_by_id(lid)
                if not l: raise AppError(400, 40005, f"Label with id {lid} not found")
                new_labels.append(l)
            entity.labels = new_labels
        try:
            updated = self.repo.update(entity)
            resp = self._map_to_dto(updated)
            cache_set(f"article:{id}", resp.model_dump_json())
            cache_delete("article_all")
            return resp
        except IntegrityError:
            self.db.rollback()
            raise AppError(403, 40303, "Article with this title already exists")

    def delete(self, id: int):
        if not self.repo.delete(id): raise AppError(404, 40402, f"Article with id {id} not found")
        try:
            requests.delete(f"{DISCUSSION_URL}/by-article/{id}", timeout=2)
        except Exception:
            pass
        cache_delete(f"article:{id}", "article_all")

    def _map_to_dto(self, entity: Article) -> ArticleResponseTo:
        label_dtos = [LabelResponseTo(id=l.id, name=l.name) for l in (entity.labels or [])]
        return ArticleResponseTo(id=entity.id, writerId=entity.writer_id, title=entity.title, content=entity.content,
                                 created=entity.created, modified=entity.modified, labels=label_dtos)


# --- СЕРВИС ПОСТОВ ---
class PostService(BaseService):
    def __init__(self, db: Session):
        super().__init__(db)
        self.article_repo = PostgresRepository(Article, db)

    def create(self, dto: PostRequestTo) -> PostResponseTo:
        article_service = ArticleService(self.db)
        try:
            article_service.get_by_id(dto.articleId)
        except AppError:
            raise AppError(400, 40004, f"Article with id {dto.articleId} does not exist")

        post_id = int(time.time() * 1000)
        msg = {"id": post_id, "articleId": dto.articleId, "content": dto.content, "state": "PENDING"}

        cache_set(f"post:{post_id}", json.dumps(msg))
        cache_delete("post_all")

        prod = get_producer()
        if prod:
            try:
                prod.send('InTopic', key=dto.articleId, value=msg)
                prod.flush()  # Ждем физической отправки!
            except Exception:
                pass

        return PostResponseTo(**msg)

    def get_all(self) -> list[PostResponseTo]:
        cached = cache_get("post_all")
        if cached: return [PostResponseTo.model_validate(item) for item in json.loads(cached)]
        try:
            resp = requests.get(DISCUSSION_URL, timeout=2)
            if resp.status_code == 200:
                data = resp.json()
                cache_set("post_all", json.dumps(data))
                return [PostResponseTo(**p) for p in data]
        except Exception:
            pass
        return []

    def get_by_id(self, id: int) -> PostResponseTo:
        cached = cache_get(f"post:{id}")
        if cached: return PostResponseTo.model_validate_json(cached)
        try:
            resp = requests.get(f"{DISCUSSION_URL}/{id}", timeout=2)
            if resp.status_code == 200:
                data = resp.json()
                cache_set(f"post:{id}", json.dumps(data))
                return PostResponseTo(**data)
        except Exception:
            pass
        raise AppError(404, 40404, f"Post with id {id} not found")

    def update(self, id: int, dto: PostRequestTo) -> PostResponseTo:
        if not self.article_repo.find_by_id(dto.articleId): raise AppError(400, 40004,
                                                                           f"Article with id {dto.articleId} does not exist")
        try:
            resp = requests.put(f"{DISCUSSION_URL}/{id}", json=dto.model_dump(), timeout=2)
            if resp.status_code == 200:
                data = resp.json()
                cache_set(f"post:{id}", json.dumps(data))
                cache_delete("post_all")
                return PostResponseTo(**data)
        except Exception:
            pass
        raise AppError(404, 40404, f"Post with id {id} not found")

    def delete(self, id: int):
        try:
            resp = requests.delete(f"{DISCUSSION_URL}/{id}", timeout=2)
            if resp.status_code in [200, 204]:
                cache_delete(f"post:{id}", "post_all")
                return
        except Exception:
            pass
        raise AppError(404, 40404, f"Post with id {id} not found")