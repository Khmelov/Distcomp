from dataclasses import dataclass


@dataclass
class Notice:
    id: int
    story_id: int
    country: str
    content: str

    @classmethod
    def from_cassandra_row(cls, row):
        return cls(
            id=row.id,
            story_id=row.story_id,
            country=row.country,
            content=row.content
        )