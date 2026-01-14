from cassandra.cqlengine import columns
from cassandra.cqlengine.models import Model
from pydantic import BaseModel
from uuid import uuid4

# Модель Cassandra для Writer
class Writer(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    name = columns.Text()
    email = columns.Text()

# Pydantic модель для Writer (REST API)
class WriterCreate(BaseModel):
    name: str
    email: str

# Модель Cassandra для Issue
class Issue(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    title = columns.Text()
    content = columns.Text()
    author_id = columns.UUID()

# Pydantic модель для Issue
class IssueCreate(BaseModel):
    title: str
    content: str
    author_id: str

# Модель Cassandra для Comment
class Comment(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    writer_id = columns.UUID()
    issue_id = columns.UUID()
    comment_type = columns.Text()  # like, dislike, etc.

# Pydantic модель для Comment
class CommentCreate(BaseModel):
    writer_id: str
    issue_id: str
    comment_type: str

# Модель Cassandra для Marker
class Marker(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    name = columns.Text()
    issue_id = columns.UUID()

# Pydantic модель для Marker
class MarkerCreate(BaseModel):
    name: str
    issue_id: str