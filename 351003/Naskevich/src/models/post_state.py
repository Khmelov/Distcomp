from enum import StrEnum


class PostState(StrEnum):
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"
