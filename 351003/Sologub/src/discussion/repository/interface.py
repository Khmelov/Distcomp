from abc import ABC, abstractmethod
from typing import Optional, List
from dataclasses import dataclass


class NoteState:
    PENDING = "PENDING"
    APPROVE = "APPROVE"
    DECLINE = "DECLINE"


@dataclass
class NoteDto:
    id: int
    storyId: int
    content: str
    country: str = ""
    state: str = NoteState.PENDING


class NoteRepository(ABC):

    @abstractmethod
    def create(self, story_id: int, content: str, country: str = "") -> NoteDto:
        pass

    @abstractmethod
    def get_by_id(self, note_id: int) -> Optional[NoteDto]:
        pass

    @abstractmethod
    def list_by_story(self, story_id: int) -> List[NoteDto]:
        pass

    @abstractmethod
    def update(self, story_id: int, note_id: int, content: str, country: str = "") -> Optional[NoteDto]:
        pass

    @abstractmethod
    def delete(self, story_id: int, note_id: int) -> bool:
        pass
