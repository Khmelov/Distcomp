from pydantic import BaseModel, ConfigDict


class Sticker(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    name: str


class StickerID(Sticker):
    id: int