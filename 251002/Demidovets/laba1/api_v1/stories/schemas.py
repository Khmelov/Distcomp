from pydantic import BaseModel, ConfigDict


class News(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    editorId: int
    title: str
    content: str 


class NewsID(News):
    id: int