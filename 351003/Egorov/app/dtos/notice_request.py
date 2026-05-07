from pydantic import BaseModel, constr, Field


class NoticeRequestTo(BaseModel):
    content: constr(min_length=2, max_length=2048)
    story_id: int
    country: str = "Belarus"