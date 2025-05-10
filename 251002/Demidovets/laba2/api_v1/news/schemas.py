from pydantic import BaseModel, ConfigDict, Field


class News(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    editorId: int #= Field(..., alias='editor_id')
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)


class NewsBD(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    editor_id: int #= Field(..., alias='editorId')
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)

    # class Config:
    #     orm_mode = True  # Позволяет использовать ORM
    # alias_generator = lambda s: s.replace('Id', '_id')


class NewsID(News):
    id: int