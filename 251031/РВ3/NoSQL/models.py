from cassandra.cqlengine import columns
from cassandra.cqlengine.models import Model
from pydantic import BaseModel
from uuid import uuid4

# Модель Cassandra для User
class User(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    name = columns.Text()
    email = columns.Text()

# Pydantic модель для User (REST API)
class UserCreate(BaseModel):
    name: str
    email: str

# Модель Cassandra для News
class News(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    title = columns.Text()
    content = columns.Text()
    author_id = columns.UUID()

# Pydantic модель для News
class NewsCreate(BaseModel):
    title: str
    content: str
    author_id: str

# Модель Cassandra для Reaction
class Reaction(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    user_id = columns.UUID()
    news_id = columns.UUID()
    reaction_type = columns.Text()  # like, dislike, etc.

# Pydantic модель для Reaction
class ReactionCreate(BaseModel):
    user_id: str
    news_id: str
    reaction_type: str

# Модель Cassandra для Tag
class Tag(Model):
    __keyspace__ = "nosql_db"
    id = columns.UUID(primary_key=True, default=uuid4)
    name = columns.Text()
    news_id = columns.UUID()

# Pydantic модель для Tag
class TagCreate(BaseModel):
    name: str
    news_id: str