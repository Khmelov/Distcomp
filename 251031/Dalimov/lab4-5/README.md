 uvicorn app.main:app --host localhost --port 24110 --reload
 http://localhost:24110/docs
from fastapi import FastAPI, HTTPException, status, Body, Query, Depends
from pydantic import BaseModel, Field, validator, conint, constr
from typing import List, Optional, Dict, Any, Union
from enum import Enum
from datetime import datetime
from fastapi.encoders import jsonable_encoder


app = FastAPI(title="News API Service",
              description="API for managing users, news, tags and reactions",
              version="1.0.0")


# Database Models
class UserModel:
    def __init__(self):
        self.users = []
        self.last_id = 0

    def create(self, user_data):
        self.last_id += 1
        user = {"id": self.last_id, **user_data}
        self.users.append(user)
        return user

    def get_by_id(self, user_id):
        return next((u for u in self.users if u["id"] == user_id), None)

    def get_by_login(self, login):
        return next((u for u in self.users if u["login"] == login), None)

    def get_all(self):
        return self.users.copy()

    def update(self, user_id, update_data):
        user = self.get_by_id(user_id)
        if user:
            user.update(update_data)
            return user
        return None

    def delete(self, user_id):
        self.users = [u for u in self.users if u["id"] != user_id]


class NewsModel:
    def __init__(self):
        self.news = []
        self.last_id = 0

    def create(self, news_data):
        self.last_id += 1
        news = {"id": self.last_id, **news_data}
        self.news.append(news)
        return news

    def get_by_id(self, news_id):
        return next((n for n in self.news if n["id"] == news_id), None)

    def get_by_user(self, user_id):
        return [n for n in self.news if n["userId"] == user_id]

    def get_all(self):
        return self.news.copy()

    def update(self, news_id, update_data):
        news = self.get_by_id(news_id)
        if news:
            news.update(update_data)
            return news
        return None

    def delete(self, news_id):
        self.news = [n for n in self.news if n["id"] != news_id]


class TagModel:
    def __init__(self):
        self.tags = []
        self.last_id = 0

    def create(self, tag_data):
        self.last_id += 1
        tag = {"id": self.last_id, **tag_data}
        self.tags.append(tag)
        return tag

    def get_by_id(self, tag_id):
        return next((t for t in self.tags if t["id"] == tag_id), None)

    def get_by_news(self, news_id):
        return [t for t in self.tags if t["newsId"] == news_id]

    def get_by_name(self, name):
        return [t for t in self.tags if t["name"] == name]

    def get_all(self):
        return self.tags.copy()

    def delete(self, tag_id):
        self.tags = [t for t in self.tags if t["id"] != tag_id]

    def delete_by_news(self, news_id):
        self.tags = [t for t in self.tags if t["newsId"] != news_id]


class ReactionType(str, Enum):
    LIKE = "like"
    DISLIKE = "dislike"
    LOVE = "love"
    LAUGH = "laugh"
    ANGRY = "angry"


class ReactionModel:
    def __init__(self):
        self.reactions = []
        self.last_id = 0

    def create(self, reaction_data):
        self.last_id += 1
        reaction = {"id": self.last_id, **reaction_data}
        self.reactions.append(reaction)
        return reaction

    def get_by_id(self, reaction_id):
        return next((r for r in self.reactions if r["id"] == reaction_id), None)

    def get_by_news(self, news_id):
        return [r for r in self.reactions if r["newsId"] == news_id]

    def get_by_user(self, user_id):
        return [r for r in self.reactions if r["userId"] == user_id]

    def get_all(self):
        return self.reactions.copy()

    def delete(self, reaction_id):
        self.reactions = [r for r in self.reactions if r["id"] != reaction_id]

    def delete_by_news(self, news_id):
        self.reactions = [r for r in self.reactions if r["newsId"] != news_id]


# Initialize database
db_users = UserModel()
db_news = NewsModel()
db_tags = TagModel()
db_reactions = ReactionModel()


# Pydantic Models with stricter validation
class UserBase(BaseModel):
    login: constr(min_length=3, max_length=50) = Field(
        ..., example="user123", description="Unique user login")
    password: constr(min_length=6, max_length=100) = Field(
        ..., example="securepassword", description="User password")
    firstname: constr(min_length=1, max_length=50) = Field(
        ..., example="John", description="User first name")
    lastname: constr(min_length=1, max_length=50) = Field(
        ..., example="Doe", description="User last name")

    @validator('login')
    def login_alphanumeric(cls, v):
        if not v.isalnum():
            raise ValueError('Login must be alphanumeric')
        return v


class UserCreate(UserBase):
    pass


class UserResponse(UserBase):
    id: int = Field(..., example=1, description="Unique user ID")

    class Config:
        orm_mode = True


class NewsBase(BaseModel):
    title: constr(min_length=5, max_length=100) = Field(
        ..., example="Breaking News", description="News title")
    content: constr(min_length=10, max_length=5000) = Field(
        ..., example="Content of the news article", description="News content")


class NewsCreate(NewsBase):
    userId: Union[int, str] = Field(..., example=1, description="ID of the user who created the news")
    tags: Optional[str] = Field(None, example="tag1,tag2,tag3", description="Comma-separated list of tags")


class NewsResponse(NewsBase):
    id: int = Field(..., example=1, description="Unique news ID")
    userId: int = Field(..., example=1, description="ID of the user who created the news")
    tags: List[str] = Field([], example=["tag1", "tag2"], description="List of tags")
    created_at: datetime = Field(default_factory=datetime.now, description="Creation timestamp")

    class Config:
        orm_mode = True


class TagBase(BaseModel):
    name: constr(min_length=2, max_length=50) = Field(
        ..., example="politics", description="Tag name")

    @validator('name')
    def name_no_commas(cls, v):
        if ',' in v:
            raise ValueError('Tag name cannot contain commas')
        return v


class TagCreate(TagBase):
    newsId: Union[int, str] = Field(..., example=1, description="ID of the news to tag")


class TagResponse(TagBase):
    id: int = Field(..., example=1, description="Unique tag ID")
    newsId: int = Field(..., example=1, description="ID of the tagged news")

    class Config:
        orm_mode = True


class ReactionBase(BaseModel):
    reactionType: ReactionType = Field(..., example="like", description="Type of reaction")


class ReactionCreate(ReactionBase):
    newsId: Union[int, str] = Field(..., example=1, description="ID of the news being reacted to")
    userId: Union[int, str] = Field(..., example=1, description="ID of the user reacting")


class ReactionResponse(ReactionBase):
    id: int = Field(..., example=1, description="Unique reaction ID")
    newsId: int = Field(..., example=1, description="ID of the reacted news")
    userId: int = Field(..., example=1, description="ID of the user who reacted")
    created_at: datetime = Field(default_factory=datetime.now, description="Reaction timestamp")

    class Config:
        orm_mode = True


# Helper functions with improved validation
def parse_id(id_val: Union[int, str], id_type: str = "user") -> int:
    if isinstance(id_val, str) and id_val == "Id_error_in_previous_steps":
        if id_type == "user":
            return db_users.last_id
        elif id_type == "news":
            return db_news.last_id
        elif id_type == "tag":
            return db_tags.last_id
        elif id_type == "reaction":
            return db_reactions.last_id

    try:
        return int(id_val)
    except (ValueError, TypeError):
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Invalid ID format"
        )


def get_user_or_404(user_id: Union[int, str]):
    user_id_int = parse_id(user_id, "user")
    user = db_users.get_by_id(user_id_int)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )
    return user


def get_news_or_404(news_id: Union[int, str]):
    news_id_int = parse_id(news_id, "news")
    news = db_news.get_by_id(news_id_int)
    if not news:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="News not found"
        )
    return news


def get_tag_or_404(tag_id: Union[int, str]):
    tag_id_int = parse_id(tag_id, "tag")
    tag = db_tags.get_by_id(tag_id_int)
    if not tag:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Tag not found"
        )
    return tag


def get_reaction_or_404(reaction_id: Union[int, str]):
    reaction_id_int = parse_id(reaction_id, "reaction")
    reaction = db_reactions.get_by_id(reaction_id_int)
    if not reaction:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Reaction not found"
        )
    return reaction


# User endpoints with improved validation
@app.post("/api/v1.0/users",
          status_code=status.HTTP_201_CREATED,
          response_model=UserResponse,
          summary="Create a new user",
          description="Creates a new user with the provided details",
          responses={
              400: {"description": "Invalid input data"},
              403: {"description": "User with this login already exists"},
              422: {"description": "Validation error"}
          })
async def create_user(user: UserCreate):
    try:
        if db_users.get_by_login(user.login):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="User with this login already exists"
            )

        created_user = db_users.create(user.dict())
        return created_user
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(e)
        )


@app.get("/api/v1.0/users",
         response_model=List[UserResponse],
         summary="Get all users",
         description="Returns a list of all registered users")
async def get_users():
    return db_users.get_all()


@app.get("/api/v1.0/users/{user_id}",
         response_model=UserResponse,
         summary="Get user by ID",
         description="Returns details of a specific user",
         responses={
             404: {"description": "User not found"},
             422: {"description": "Invalid ID format"}
         })
async def get_user(user_id: Union[int, str]):
    return get_user_or_404(user_id)


@app.put("/api/v1.0/users",
         response_model=UserResponse,
         summary="Update user",
         description="Updates details of an existing user",
         responses={
             400: {"description": "Invalid input data"},
             403: {"description": "User with this login already exists"},
             404: {"description": "User not found"},
             422: {"description": "Validation error"}
         })
async def update_user(user_data: Dict[str, Any] = Body(...)):
    try:
        user_id = parse_id(user_data.get("id"), "user")
        user = db_users.get_by_id(user_id)
        if not user:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )

        if "login" in user_data:
            existing_user = db_users.get_by_login(user_data["login"])
            if existing_user and existing_user["id"] != user_id:
                raise HTTPException(
                    status_code=status.HTTP_403_FORBIDDEN,
                    detail="User with this login already exists"
                )

        updated_user = db_users.update(user_id, user_data)
        if not updated_user:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update user"
            )

        return updated_user
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(e)
        )


@app.delete("/api/v1.0/users/{user_id}",
            status_code=status.HTTP_204_NO_CONTENT,
            summary="Delete user",
            description="Deletes a user and all their associated data",
            responses={
                404: {"description": "User not found"},
                422: {"description": "Invalid ID format"}
            })
async def delete_user(user_id: Union[int, str]):
    user_id_int = parse_id(user_id, "user")
    user = db_users.get_by_id(user_id_int)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="User not found"
        )

    # Delete user's news, tags and reactions
    user_news = db_news.get_by_user(user_id_int)
    for news in user_news:
        db_tags.delete_by_news(news["id"])
        db_reactions.delete_by_news(news["id"])
        db_news.delete(news["id"])

    db_users.delete(user_id_int)


# News endpoints with improved validation
@app.post("/api/v1.0/news",
          status_code=status.HTTP_201_CREATED,
          response_model=NewsResponse,
          summary="Create news article",
          description="Creates a new news article with optional tags",
          responses={
              400: {"description": "Invalid input data"},
              403: {"description": "News with this title already exists for this user"},
              404: {"description": "User not found"},
              422: {"description": "Validation error"}
          })
async def create_news(news_data: Dict[str, Any] = Body(...)):
    try:
        # Validate required fields
        if not all(k in news_data for k in ["title", "content", "userId"]):
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Missing required fields"
            )

        user_id = parse_id(news_data.get("userId"), "user")
        title = news_data.get("title", "").strip()
        content = news_data.get("content", "").strip()

        if not title or not content:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Title and content cannot be empty"
            )

        if len(title) < 5 or len(title) > 100:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Title must be between 5 and 100 characters"
            )

        if len(content) < 10 or len(content) > 5000:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Content must be between 10 and 5000 characters"
            )

        if not db_users.get_by_id(user_id):
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )

        # Check for duplicate title for this user
        if any(n["title"] == title and n["userId"] == user_id for n in db_news.get_all()):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="News with this title already exists for this user"
            )

        # Create news without tags first
        news_dict = {
            "userId": user_id,
            "title": title,
            "content": content,
            "created_at": datetime.now()
        }
        created_news = db_news.create(news_dict)

        # Process tags if provided
        tags = []
        tags_str = news_data.get("tags", "")
        if tags_str:
            tag_names = [name.strip() for name in tags_str.split(",") if name.strip()]
            for tag_name in tag_names:
                if len(tag_name) < 2 or len(tag_name) > 50:
                    continue  # Skip invalid tags
                if ',' in tag_name:
                    continue  # Skip tags with commas
                db_tags.create({
                    "name": tag_name,
                    "newsId": created_news["id"]
                })
                tags.append(tag_name)

        return {
            **created_news,
            "tags": tags
        }
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(e)
        )


@app.get("/api/v1.0/news",
         response_model=List[NewsResponse],
         summary="Get all news",
         description="Returns a list of all news articles with their tags")
async def get_all_news():
    news_list = db_news.get_all()
    result = []
    for news in news_list:
        tags = [t["name"] for t in db_tags.get_by_news(news["id"])]
        result.append({
            **news,
            "tags": tags
        })
    return result


@app.get("/api/v1.0/news/{news_id}",
         response_model=NewsResponse,
         summary="Get news by ID",
         description="Returns a specific news article with its tags",
         responses={
             404: {"description": "News not found"},
             422: {"description": "Invalid ID format"}
         })
async def get_news(news_id: Union[int, str]):
    news_id_int = parse_id(news_id, "news")
    news = db_news.get_by_id(news_id_int)
    if not news:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="News not found"
        )

    tags = [t["name"] for t in db_tags.get_by_news(news_id_int)]
    return {
        **news,
        "tags": tags
    }


@app.put("/api/v1.0/news",
         response_model=NewsResponse,
         summary="Update news",
         description="Updates an existing news article",
         responses={
             400: {"description": "Invalid input data"},
             403: {"description": "News with this title already exists for this user"},
             404: {"description": "News or User not found"},
             422: {"description": "Validation error"}
         })
async def update_news(news_data: Dict[str, Any] = Body(...)):
    try:
        if not all(k in news_data for k in ["id", "userId"]):
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Missing required fields"
            )

        news_id = parse_id(news_data.get("id"), "news")
        user_id = parse_id(news_data.get("userId"), "user")

        if not db_users.get_by_id(user_id):
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )

        news = db_news.get_by_id(news_id)
        if not news:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="News not found"
            )

        title = news_data.get("title", news["title"]).strip()
        content = news_data.get("content", news["content"]).strip()

        if title and (len(title) < 5 or len(title) > 100):
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Title must be between 5 and 100 characters"
            )

        if content and (len(content) < 10 or len(content) > 5000):
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Content must be between 10 and 5000 characters"
            )

        # Check for duplicate title for this user (excluding current news)
        if (title and
                any(n["title"] == title and n["userId"] == user_id and n["id"] != news_id
                    for n in db_news.get_all())):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="News with this title already exists for this user"
            )

        update_dict = {
            "title": title,
            "content": content,
            "userId": user_id
        }

        updated_news = db_news.update(news_id, update_dict)
        if not updated_news:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                detail="Failed to update news"
            )

        tags = [t["name"] for t in db_tags.get_by_news(news_id)]
        return {
            **updated_news,
            "tags": tags
        }
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(e)
        )


@app.delete("/api/v1.0/news/{news_id}",
            status_code=status.HTTP_204_NO_CONTENT,
            summary="Delete news",
            description="Deletes a news article and its associated tags and reactions",
            responses={
                404: {"description": "News not found"},
                422: {"description": "Invalid ID format"}
            })
async def delete_news(news_id: Union[int, str]):
    news_id_int = parse_id(news_id, "news")
    news = db_news.get_by_id(news_id_int)
    if not news:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="News not found"
        )

    db_tags.delete_by_news(news_id_int)
    db_reactions.delete_by_news(news_id_int)
    db_news.delete(news_id_int)


# Tag endpoints with improved validation
@app.post("/api/v1.0/tags",
          status_code=status.HTTP_201_CREATED,
          response_model=TagResponse,
          summary="Create tag",
          description="Creates a new tag for a news article",
          responses={
              400: {"description": "Invalid input data"},
              403: {"description": "Tag with this name already exists for this news"},
              404: {"description": "News not found"},
              422: {"description": "Validation error"}
          })
async def create_tag(tag_data: Dict[str, Any] = Body(...)):
    try:
        if not all(k in tag_data for k in ["name", "newsId"]):
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Missing required fields"
            )

        news_id = parse_id(tag_data.get("newsId"), "news")
        tag_name = tag_data.get("name", "").strip()

        if not tag_name:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Tag name cannot be empty"
            )

        if len(tag_name) < 2 or len(tag_name) > 50:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Tag name must be between 2 and 50 characters"
            )

        if ',' in tag_name:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Tag name cannot contain commas"
            )

        if not db_news.get_by_id(news_id):
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="News not found"
            )

        if any(t["name"] == tag_name and t["newsId"] == news_id for t in db_tags.get_all()):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Tag with this name already exists for this news"
            )

        created_tag = db_tags.create({
            "name": tag_name,
            "newsId": news_id
        })
        return created_tag
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(e)
        )


@app.get("/api/v1.0/tags",
         response_model=List[TagResponse],
         summary="Get all tags",
         description="Returns a list of all tags")
async def get_all_tags():
    return db_tags.get_all()


@app.get("/api/v1.0/tags/{tag_id}",
         response_model=TagResponse,
         summary="Get tag by ID",
         description="Returns details of a specific tag",
         responses={
             404: {"description": "Tag not found"},
             422: {"description": "Invalid ID format"}
         })
async def get_tag(tag_id: Union[int, str]):
    return get_tag_or_404(tag_id)


@app.delete("/api/v1.0/tags/{tag_id}",
            status_code=status.HTTP_204_NO_CONTENT,
            summary="Delete tag",
            description="Deletes a specific tag",
            responses={
                404: {"description": "Tag not found"},
                422: {"description": "Invalid ID format"}
            })
async def delete_tag(tag_id: Union[int, str]):
    tag_id_int = parse_id(tag_id, "tag")
    tag = db_tags.get_by_id(tag_id_int)
    if not tag:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Tag not found"
        )
    db_tags.delete(tag_id_int)


# Reaction endpoints with improved validation
@app.post("/api/v1.0/reactions",
          status_code=status.HTTP_201_CREATED,
          response_model=ReactionResponse,
          summary="Create reaction",
          description="Adds a reaction to a news article",
          responses={
              400: {"description": "Invalid input data"},
              403: {"description": "User already reacted to this news"},
              404: {"description": "News or User not found"},
              422: {"description": "Validation error"}
          })
async def create_reaction(reaction_data: Dict[str, Any] = Body(...)):
    try:
        if not all(k in reaction_data for k in ["reactionType", "newsId", "userId"]):
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Missing required fields"
            )

        news_id = parse_id(reaction_data.get("newsId"), "news")
        user_id = parse_id(reaction_data.get("userId"), "user")
        reaction_type = reaction_data.get("reactionType")

        if not db_news.get_by_id(news_id):
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="News not found"
            )

        if not db_users.get_by_id(user_id):
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )

        if not reaction_type or reaction_type not in ReactionType.__members__:
            raise HTTPException(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                detail="Invalid reaction type"
            )

        # Check if user already reacted to this news
        existing_reaction = next(
            (r for r in db_reactions.get_by_news(news_id)
             if r["userId"] == user_id),
            None
        )

        if existing_reaction:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="User already reacted to this news"
            )

        created_reaction = db_reactions.create({
            "newsId": news_id,
            "userId": user_id,
            "reactionType": reaction_type,
            "created_at": datetime.now()
        })
        return created_reaction
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=str(e)
        )


@app.get("/api/v1.0/reactions",
         response_model=List[ReactionResponse],
         summary="Get all reactions",
         description="Returns a list of all reactions")
async def get_all_reactions():
    return db_reactions.get_all()


@app.get("/api/v1.0/reactions/{reaction_id}",
         response_model=ReactionResponse,
         summary="Get reaction by ID",
         description="Returns details of a specific reaction",
         responses={
             404: {"description": "Reaction not found"},
             422: {"description": "Invalid ID format"}
         })
async def get_reaction(reaction_id: Union[int, str]):
    return get_reaction_or_404(reaction_id)


@app.delete("/api/v1.0/reactions/{reaction_id}",
            status_code=status.HTTP_204_NO_CONTENT,
            summary="Delete reaction",
            description="Deletes a specific reaction",
            responses={
                404: {"description": "Reaction not found"},
                422: {"description": "Invalid ID format"}
            })
async def delete_reaction(reaction_id: Union[int, str]):
    reaction_id_int = parse_id(reaction_id, "reaction")
    reaction = db_reactions.get_by_id(reaction_id_int)
    if not reaction:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Reaction not found"
        )
    db_reactions.delete(reaction_id_int)