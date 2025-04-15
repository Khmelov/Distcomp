from datetime import datetime

from pydantic import BaseModel, Field

class StoryBase(BaseModel):
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)
    creator_id: int = Field(alias="creatorId")

    model_config = {
        'from_attributes': True,
        'populate_by_name': True,
    }


class StoryRequestToAdd(StoryBase):
    pass

class StoryRequestToUpdate(StoryRequestToAdd):
    id: int

class StoryResponseTo(StoryBase):
    id: int
    created: datetime
    modified: datetime
