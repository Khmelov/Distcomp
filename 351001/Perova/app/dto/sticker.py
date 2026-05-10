from pydantic import AliasChoices, BaseModel, ConfigDict, Field


class StickerRequestTo(BaseModel):
    model_config = ConfigDict(populate_by_name=True, str_strip_whitespace=True)

    id: int | None = None
    name: str = Field(
        min_length=2,
        max_length=32,
        validation_alias=AliasChoices("name", "Name", "stickerName", "label"),
    )


class StickerResponseTo(BaseModel):
    id: int
    name: str
