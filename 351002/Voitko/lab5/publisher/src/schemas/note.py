from pydantic import BaseModel, ConfigDict, Field
from pydantic.alias_generators import to_camel


class NoteBase(BaseModel):
    content: str = Field(min_length=2, max_length=2048)
    news_id: int

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, validate_by_name=True)

class NoteRequestTo(NoteBase):
    pass

class NoteResponseTo(NoteBase):
    id: int