from cassandra.cluster import Cluster
from fastapi import FastAPI, HTTPException, status
from contextlib import asynccontextmanager
from pydantic import BaseModel
from uuid import UUID, uuid4
from typing import List, Optional
import uvicorn


# Модели данных
class UserCreate(BaseModel):
    login: str
    password: str
    firstname: str
    lastname: str


class User(UserCreate):
    id: UUID


class NewsCreate(BaseModel):
    userId: UUID
    title: str
    content: str


class News(NewsCreate):
    id: UUID


class ReactionCreate(BaseModel):
    newsId: UUID
    content: str


class Reaction(ReactionCreate):
    id: UUID


class TagCreate(BaseModel):
    name: str


class Tag(TagCreate):
    id: UUID


@asynccontextmanager
async def lifespan(app: FastAPI):
    # Подключение к Cassandra
    app.state.cluster = Cluster(['localhost'], port=9042)
    app.state.session = app.state.cluster.connect()

    # Создание keyspace и таблиц
    app.state.session.execute("""
        CREATE KEYSPACE IF NOT EXISTS distcomp 
        WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1}
    """)
    app.state.session.set_keyspace("distcomp")

    # Таблица пользователей
    app.state.session.execute("""
        CREATE TABLE IF NOT EXISTS tbl_user (
            id UUID PRIMARY KEY,
            login TEXT,
            password TEXT,
            firstname TEXT,
            lastname TEXT
        )
    """)

    # Таблица новостей
    app.state.session.execute("""
        CREATE TABLE IF NOT EXISTS tbl_news (
            id UUID PRIMARY KEY,
            userId UUID,
            title TEXT,
            content TEXT
        )
    """)

    # Таблица реакций
    app.state.session.execute("""
        CREATE TABLE IF NOT EXISTS tbl_reaction (
            id UUID PRIMARY KEY,
            newsId UUID,
            content TEXT
        )
    """)

    # Таблица тегов
    app.state.session.execute("""
        CREATE TABLE IF NOT EXISTS tbl_tag (
            id UUID PRIMARY KEY,
            name TEXT
        )
    """)

    yield

    # Закрытие соединения
    app.state.cluster.shutdown()


app = FastAPI(lifespan=lifespan, title="DistComp API", version="1.0.0")


# Users Endpoints
@app.post("/api/v1.0/users", response_model=User, status_code=status.HTTP_201_CREATED)
async def create_user(user: UserCreate):
    user_id = uuid4()
    query = """
        INSERT INTO tbl_user (id, login, password, firstname, lastname)
        VALUES (%s, %s, %s, %s, %s)
    """
    app.state.session.execute(query, (user_id, user.login, user.password, user.firstname, user.lastname))
    return {"id": user_id, **user.dict()}


@app.get("/api/v1.0/users/{user_id}", response_model=User)
async def get_user(user_id: UUID):
    query = "SELECT id, login, password, firstname, lastname FROM tbl_user WHERE id = %s"
    row = app.state.session.execute(query, (user_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="User not found")
    return dict(row)


@app.get("/api/v1.0/users", response_model=List[User])
async def get_users():
    rows = app.state.session.execute("SELECT id, login, password, firstname, lastname FROM tbl_user")
    return [dict(row) for row in rows]


@app.put("/api/v1.0/users", response_model=User)
async def update_user(user: User):
    query = """
        UPDATE tbl_user 
        SET login = %s, password = %s, firstname = %s, lastname = %s
        WHERE id = %s
    """
    app.state.session.execute(query, (user.login, user.password, user.firstname, user.lastname, user.id))
    return user


@app.delete("/api/v1.0/users/{user_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_user(user_id: UUID):
    app.state.session.execute("DELETE FROM tbl_user WHERE id = %s", (user_id,))
    return None


# News Endpoints
@app.post("/api/v1.0/news", response_model=News, status_code=status.HTTP_201_CREATED)
async def create_news(news: NewsCreate):
    news_id = uuid4()
    query = """
        INSERT INTO tbl_news (id, userId, title, content)
        VALUES (%s, %s, %s, %s)
    """
    app.state.session.execute(query, (news_id, news.userId, news.title, news.content))
    return {"id": news_id, **news.dict()}


@app.get("/api/v1.0/news/{news_id}", response_model=News)
async def get_news(news_id: UUID):
    query = "SELECT id, userId, title, content FROM tbl_news WHERE id = %s"
    row = app.state.session.execute(query, (news_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="News not found")
    return dict(row)


@app.get("/api/v1.0/news", response_model=List[News])
async def get_all_news():
    rows = app.state.session.execute("SELECT id, userId, title, content FROM tbl_news")
    return [dict(row) for row in rows]


@app.put("/api/v1.0/news", response_model=News)
async def update_news(news: News):
    query = """
        UPDATE tbl_news 
        SET userId = %s, title = %s, content = %s
        WHERE id = %s
    """
    app.state.session.execute(query, (news.userId, news.title, news.content, news.id))
    return news


@app.delete("/api/v1.0/news/{news_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_news(news_id: UUID):
    app.state.session.execute("DELETE FROM tbl_news WHERE id = %s", (news_id,))
    return None


# Reactions Endpoints
@app.post("/api/v1.0/reactions", response_model=Reaction, status_code=status.HTTP_201_CREATED)
async def create_reaction(reaction: ReactionCreate):
    reaction_id = uuid4()
    query = """
        INSERT INTO tbl_reaction (id, newsId, content)
        VALUES (%s, %s, %s)
    """
    app.state.session.execute(query, (reaction_id, reaction.newsId, reaction.content))
    return {"id": reaction_id, **reaction.dict()}


@app.get("/api/v1.0/reactions/{reaction_id}", response_model=Reaction)
async def get_reaction(reaction_id: UUID):
    query = "SELECT id, newsId, content FROM tbl_reaction WHERE id = %s"
    row = app.state.session.execute(query, (reaction_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Reaction not found")
    return dict(row)


@app.get("/api/v1.0/reactions", response_model=List[Reaction])
async def get_all_reactions():
    rows = app.state.session.execute("SELECT id, newsId, content FROM tbl_reaction")
    return [dict(row) for row in rows]


@app.put("/api/v1.0/reactions", response_model=Reaction)
async def update_reaction(reaction: Reaction):
    query = """
        UPDATE tbl_reaction 
        SET newsId = %s, content = %s
        WHERE id = %s
    """
    app.state.session.execute(query, (reaction.newsId, reaction.content, reaction.id))
    return reaction


@app.delete("/api/v1.0/reactions/{reaction_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_reaction(reaction_id: UUID):
    app.state.session.execute("DELETE FROM tbl_reaction WHERE id = %s", (reaction_id,))
    return None


# Tags Endpoints
@app.post("/api/v1.0/tags", response_model=Tag, status_code=status.HTTP_201_CREATED)
async def create_tag(tag: TagCreate):
    tag_id = uuid4()
    query = """
        INSERT INTO tbl_tag (id, name)
        VALUES (%s, %s)
    """
    app.state.session.execute(query, (tag_id, tag.name))
    return {"id": tag_id, **tag.dict()}


@app.get("/api/v1.0/tags/{tag_id}", response_model=Tag)
async def get_tag(tag_id: UUID):
    query = "SELECT id, name FROM tbl_tag WHERE id = %s"
    row = app.state.session.execute(query, (tag_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Tag not found")
    return dict(row)


@app.get("/api/v1.0/tags", response_model=List[Tag])
async def get_all_tags():
    rows = app.state.session.execute("SELECT id, name FROM tbl_tag")
    return [dict(row) for row in rows]


@app.put("/api/v1.0/tags", response_model=Tag)
async def update_tag(tag: Tag):
    query = """
        UPDATE tbl_tag 
        SET name = %s
        WHERE id = %s
    """
    app.state.session.execute(query, (tag.name, tag.id))
    return tag


@app.delete("/api/v1.0/tags/{tag_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_tag(tag_id: UUID):
    app.state.session.execute("DELETE FROM tbl_tag WHERE id = %s", (tag_id,))
    return None


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24110)