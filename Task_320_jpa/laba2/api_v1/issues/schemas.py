from pydantic import BaseModel, ConfigDict, Field


class Issue(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    writerId: int #= Field(..., alias='writer_id')
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)


class IssueBD(BaseModel):
    model_config = ConfigDict(from_attributes=True)
    writer_id: int #= Field(..., alias='writerId')
    title: str = Field(min_length=2, max_length=64)
    content: str = Field(min_length=4, max_length=2048)

    # class Config:
    #     orm_mode = True  # Позволяет использовать ORM
    # alias_generator = lambda s: s.replace('Id', '_id')


class IssueID(Issue):
    id: int