from pydantic import BaseModel, ConfigDict, Field


class Story(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    creatorId: int #= Field(..., alias='creator_id')
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)


class StoryBD(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    creator_id: int #= Field(..., alias='creatorId')
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)

    # class Config:
    #     orm_mode = True  # Позволяет использовать ORM
    # alias_generator = lambda s: s.replace('Id', '_id')


class StoryID(Story):
    id: int