from pydantic import BaseModel, ConfigDict


class Issue(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    writerId: int
    title: str
    content: str 


class IssueID(Issue):
    id: int