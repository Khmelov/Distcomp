from pydantic import BaseModel, ConfigDict


class Message(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    storyId: int
    content: str


class MessageID(Message):
    id: int