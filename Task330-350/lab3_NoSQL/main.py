from cassandra.cluster import Cluster
from fastapi import FastAPI, HTTPException, status, Path
from contextlib import asynccontextmanager
from pydantic import BaseModel
from uuid import UUID, uuid4
from typing import List, Optional
import uvicorn


# Модели данных
class WriterCreate(BaseModel):
    login: str
    password: str
    firstname: str
    lastname: str


class WriterUpdate(BaseModel):
    login: Optional[str] = None
    password: Optional[str] = None
    firstname: Optional[str] = None
    lastname: Optional[str] = None


class WriterResponse(BaseModel):
    id: UUID
    login: str
    password: str
    firstname: str
    lastname: str


class IssueCreate(BaseModel):
    writerId: UUID
    title: str
    content: str


class IssueUpdate(BaseModel):
    writerId: Optional[UUID] = None
    title: Optional[str] = None
    content: Optional[str] = None


class IssueResponse(BaseModel):
    id: UUID
    writerId: UUID
    title: str
    content: str


class CommentCreate(BaseModel):
    issueId: UUID
    content: str


class CommentUpdate(BaseModel):
    issueId: Optional[UUID] = None
    content: Optional[str] = None


class CommentResponse(BaseModel):
    id: UUID
    issueId: UUID
    content: str


class MarkerCreate(BaseModel):
    name: str


class MarkerUpdate(BaseModel):
    name: Optional[str] = None


class MarkerResponse(BaseModel):
    id: UUID
    name: str


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
        CREATE TABLE IF NOT EXISTS tbl_writer (
            id UUID PRIMARY KEY,
            login TEXT,
            password TEXT,
            firstname TEXT,
            lastname TEXT
        )
    """)

    # Таблица новостей
    app.state.session.execute("""
        CREATE TABLE IF NOT EXISTS tbl_issue (
            id UUID PRIMARY KEY,
            writerId UUID,
            title TEXT,
            content TEXT
        )
    """)

    # Таблица реакций
    app.state.session.execute("""
        CREATE TABLE IF NOT EXISTS tbl_comment (
            id UUID PRIMARY KEY,
            issueId UUID,
            content TEXT
        )
    """)

    # Таблица тегов
    app.state.session.execute("""
        CREATE TABLE IF NOT EXISTS tbl_marker (
            id UUID PRIMARY KEY,
            name TEXT
        )
    """)

    yield

    # Закрытие соединения
    app.state.cluster.shutdown()


app = FastAPI(lifespan=lifespan, title="DistComp API", version="1.0.0")


# Writers Endpoints
@app.post("/api/v1.0/writers", response_model=WriterResponse, status_code=status.HTTP_201_CREATED)
async def create_writer(writer: WriterCreate):
    writer_id = uuid4()
    query = """
        INSERT INTO tbl_writer (id, login, password, firstname, lastname)
        VALUES (%s, %s, %s, %s, %s)
    """
    app.state.session.execute(query, (writer_id, writer.login, writer.password, 
                                     writer.firstname, writer.lastname))
    return {"id": writer_id, **writer.dict()}


@app.get("/api/v1.0/writers/{writer_id}", response_model=WriterResponse)
async def get_writer(writer_id: UUID = Path(..., description="ID автора")):
    query = "SELECT id, login, password, firstname, lastname FROM tbl_writer WHERE id = %s"
    row = app.state.session.execute(query, (writer_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Writer not found")
    return dict(row)


@app.get("/api/v1.0/writers", response_model=List[WriterResponse])
async def get_writers():
    rows = app.state.session.execute(
        "SELECT id, login, password, firstname, lastname FROM tbl_writer"
    )
    return [dict(row) for row in rows]


@app.put("/api/v1.0/writers/{writer_id}", response_model=WriterResponse)
async def update_writer(
    writer_id: UUID = Path(..., description="ID автора"),
    writer_update: WriterUpdate = None
):
    # Сначала получаем текущие данные
    query_get = "SELECT * FROM tbl_writer WHERE id = %s"
    row = app.state.session.execute(query_get, (writer_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Writer not found")
    
    current_data = dict(row)
    
    # Обновляем только предоставленные поля
    updated_data = current_data.copy()
    if writer_update:
        for field, value in writer_update.dict(exclude_unset=True).items():
            if value is not None:
                updated_data[field] = value
    
    query_update = """
        UPDATE tbl_writer 
        SET login = %s, password = %s, firstname = %s, lastname = %s
        WHERE id = %s
    """
    app.state.session.execute(query_update, (
        updated_data['login'],
        updated_data['password'],
        updated_data['firstname'],
        updated_data['lastname'],
        writer_id
    ))
    
    return {"id": writer_id, **updated_data}


@app.delete("/api/v1.0/writers/{writer_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_writer(writer_id: UUID = Path(..., description="ID автора")):
    # Проверяем существование
    query_check = "SELECT id FROM tbl_writer WHERE id = %s"
    row = app.state.session.execute(query_check, (writer_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Writer not found")
    
    app.state.session.execute("DELETE FROM tbl_writer WHERE id = %s", (writer_id,))
    return None


# Issue Endpoints
@app.post("/api/v1.0/issues", response_model=IssueResponse, status_code=status.HTTP_201_CREATED)
async def create_issue(issue: IssueCreate):
    issue_id = uuid4()
    query = """
        INSERT INTO tbl_issue (id, writerId, title, content)
        VALUES (%s, %s, %s, %s)
    """
    app.state.session.execute(query, (issue_id, issue.writerId, issue.title, issue.content))
    return {"id": issue_id, **issue.dict()}


@app.get("/api/v1.0/issues/{issue_id}", response_model=IssueResponse)
async def get_issue(issue_id: UUID = Path(..., description="ID новости")):
    query = "SELECT id, writerId, title, content FROM tbl_issue WHERE id = %s"
    row = app.state.session.execute(query, (issue_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Issue not found")
    return dict(row)


@app.get("/api/v1.0/issues", response_model=List[IssueResponse])
async def get_all_issue():
    rows = app.state.session.execute("SELECT id, writerId, title, content FROM tbl_issue")
    return [dict(row) for row in rows]


@app.put("/api/v1.0/issues/{issue_id}", response_model=IssueResponse)
async def update_issue(
    issue_id: UUID = Path(..., description="ID новости"),
    issue_update: IssueUpdate = None
):
    # Получаем текущие данные
    query_get = "SELECT * FROM tbl_issue WHERE id = %s"
    row = app.state.session.execute(query_get, (issue_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Issue not found")
    
    current_data = dict(row)
    
    # Обновляем только предоставленные поля
    updated_data = current_data.copy()
    if issue_update:
        for field, value in issue_update.dict(exclude_unset=True).items():
            if value is not None:
                updated_data[field] = value
    
    query_update = """
        UPDATE tbl_issue 
        SET writerId = %s, title = %s, content = %s
        WHERE id = %s
    """
    app.state.session.execute(query_update, (
        updated_data['writerId'],
        updated_data['title'],
        updated_data['content'],
        issue_id
    ))
    
    return {"id": issue_id, **updated_data}


@app.delete("/api/v1.0/issues/{issue_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_issue(issue_id: UUID = Path(..., description="ID новости")):
    # Проверяем существование
    query_check = "SELECT id FROM tbl_issue WHERE id = %s"
    row = app.state.session.execute(query_check, (issue_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Issue not found")
    
    app.state.session.execute("DELETE FROM tbl_issue WHERE id = %s", (issue_id,))
    return None


# Comments Endpoints
@app.post("/api/v1.0/comments", response_model=CommentResponse, status_code=status.HTTP_201_CREATED)
async def create_comment(comment: CommentCreate):
    comment_id = uuid4()
    query = """
        INSERT INTO tbl_comment (id, issueId, content)
        VALUES (%s, %s, %s)
    """
    app.state.session.execute(query, (comment_id, comment.issueId, comment.content))
    return {"id": comment_id, **comment.dict()}


@app.get("/api/v1.0/comments/{comment_id}", response_model=CommentResponse)
async def get_comment(comment_id: UUID = Path(..., description="ID комментария")):
    query = "SELECT id, issueId, content FROM tbl_comment WHERE id = %s"
    row = app.state.session.execute(query, (comment_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Comment not found")
    return dict(row)


@app.get("/api/v1.0/comments", response_model=List[CommentResponse])
async def get_all_comments():
    rows = app.state.session.execute("SELECT id, issueId, content FROM tbl_comment")
    return [dict(row) for row in rows]


@app.put("/api/v1.0/comments/{comment_id}", response_model=CommentResponse)
async def update_comment(
    comment_id: UUID = Path(..., description="ID комментария"),
    comment_update: CommentUpdate = None
):
    # Получаем текущие данные
    query_get = "SELECT * FROM tbl_comment WHERE id = %s"
    row = app.state.session.execute(query_get, (comment_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Comment not found")
    
    current_data = dict(row)
    
    # Обновляем только предоставленные поля
    updated_data = current_data.copy()
    if comment_update:
        for field, value in comment_update.dict(exclude_unset=True).items():
            if value is not None:
                updated_data[field] = value
    
    query_update = """
        UPDATE tbl_comment 
        SET issueId = %s, content = %s
        WHERE id = %s
    """
    app.state.session.execute(query_update, (
        updated_data['issueId'],
        updated_data['content'],
        comment_id
    ))
    
    return {"id": comment_id, **updated_data}


@app.delete("/api/v1.0/comments/{comment_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_comment(comment_id: UUID = Path(..., description="ID комментария")):
    # Проверяем существование
    query_check = "SELECT id FROM tbl_comment WHERE id = %s"
    row = app.state.session.execute(query_check, (comment_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Comment not found")
    
    app.state.session.execute("DELETE FROM tbl_comment WHERE id = %s", (comment_id,))
    return None


# Markers Endpoints
@app.post("/api/v1.0/markers", response_model=MarkerResponse, status_code=status.HTTP_201_CREATED)
async def create_marker(marker: MarkerCreate):
    marker_id = uuid4()
    query = """
        INSERT INTO tbl_marker (id, name)
        VALUES (%s, %s)
    """
    app.state.session.execute(query, (marker_id, marker.name))
    return {"id": marker_id, **marker.dict()}


@app.get("/api/v1.0/markers/{marker_id}", response_model=MarkerResponse)
async def get_marker(marker_id: UUID = Path(..., description="ID маркера")):
    query = "SELECT id, name FROM tbl_marker WHERE id = %s"
    row = app.state.session.execute(query, (marker_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Marker not found")
    return dict(row)


@app.get("/api/v1.0/markers", response_model=List[MarkerResponse])
async def get_all_markers():
    rows = app.state.session.execute("SELECT id, name FROM tbl_marker")
    return [dict(row) for row in rows]


@app.put("/api/v1.0/markers/{marker_id}", response_model=MarkerResponse)
async def update_marker(
    marker_id: UUID = Path(..., description="ID маркера"),
    marker_update: MarkerUpdate = None
):
    # Получаем текущие данные
    query_get = "SELECT * FROM tbl_marker WHERE id = %s"
    row = app.state.session.execute(query_get, (marker_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Marker not found")
    
    current_data = dict(row)
    
    # Обновляем только предоставленные поля
    updated_data = current_data.copy()
    if marker_update:
        for field, value in marker_update.dict(exclude_unset=True).items():
            if value is not None:
                updated_data[field] = value
    
    query_update = """
        UPDATE tbl_marker 
        SET name = %s
        WHERE id = %s
    """
    app.state.session.execute(query_update, (updated_data['name'], marker_id))
    
    return {"id": marker_id, **updated_data}


@app.delete("/api/v1.0/markers/{marker_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_marker(marker_id: UUID = Path(..., description="ID маркера")):
    # Проверяем существование
    query_check = "SELECT id FROM tbl_marker WHERE id = %s"
    row = app.state.session.execute(query_check, (marker_id,)).one()
    if not row:
        raise HTTPException(status_code=404, detail="Marker not found")
    
    app.state.session.execute("DELETE FROM tbl_marker WHERE id = %s", (marker_id,))
    return None


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24110)