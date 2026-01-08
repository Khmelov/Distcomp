from sqlalchemy import Table, ForeignKey, Column
from .base import Base



Marker_issue  = Table(
    "tbl_issuemarker",
    Base.metadata,
    Column("issueId", ForeignKey("tbl_issue.id")),
    Column("markerId", ForeignKey("tbl_marker.id")),
)



