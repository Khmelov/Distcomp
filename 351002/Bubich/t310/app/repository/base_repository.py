from abc import ABC, abstractmethod
from typing import List, Optional


class BaseRepository(ABC):
    @abstractmethod
    def save(self, entity) -> object:
        pass

    @abstractmethod
    def find_by_id(self, id: int) -> Optional[object]:
        pass

    @abstractmethod
    def find_all(self) -> List[object]:
        pass

    @abstractmethod
    def update(self, entity) -> object:
        pass

    @abstractmethod
    def delete_by_id(self, id: int) -> bool:
        pass