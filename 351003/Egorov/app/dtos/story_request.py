from typing import List, Optional

from pydantic import BaseModel, conlist, constr


class StoryRequestTo(BaseModel):
    title: constr(min_length=1, max_length=200)
    content: constr(min_length=1)
    creator_id: int
    marker_ids: Optional[conlist(int, min_length=0)] = None

