from pydantic import BaseModel, ConfigDict


class Story(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    creatorId: int
    title: str
    content: str 


class StoryID(Story):
    id: int