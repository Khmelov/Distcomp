from pydantic import BaseModel, ConfigDict
from pydantic.alias_generators import to_camel


class NoteBase(BaseModel):
    content: str
    topic_id: int

    model_config = ConfigDict(from_attributes=True, alias_generator=to_camel, validate_by_name=True)

class NoteRequestTo(NoteBase):
    pass

class NoteResponseTo(NoteBase):
    id: int