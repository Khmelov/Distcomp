from typing import Literal

from pydantic import BaseModel, Field

from src.models.post_state import PostState


class PostPayload(BaseModel):
    id: int
    tweet_id: int = Field(alias="tweetId")
    content: str
    state: PostState

    model_config = {"populate_by_name": True}


PostOperation = Literal["CREATE", "GET", "GET_ALL", "UPDATE", "DELETE"]


class PostCommandMessage(BaseModel):
    correlation_id: str | None = None
    operation: PostOperation
    post: PostPayload | None = None
    post_id: int | None = None
    tweet_id: int | None = Field(default=None, alias="tweetId")
    content: str | None = None

    model_config = {"populate_by_name": True}


class PostReplyMessage(BaseModel):
    correlation_id: str
    status_code: int = 200
    post: PostPayload | None = None
    posts: list[PostPayload] | None = None
    error: str | None = None
