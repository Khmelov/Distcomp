from abc import ABC, abstractmethod

from src.models.editor import Editor


class AbstractEditorRepository(ABC):

    @abstractmethod
    async def get_by_id(self, entity_id: int) -> Editor | None:
        raise NotImplementedError

    @abstractmethod
    async def get_all(self) -> list[Editor]:
        raise NotImplementedError

    @abstractmethod
    async def create(self, entity: Editor) -> Editor:
        raise NotImplementedError

    @abstractmethod
    async def update(self, entity: Editor) -> Editor | None:
        raise NotImplementedError

    @abstractmethod
    async def delete(self, entity_id: int) -> bool:
        raise NotImplementedError

    @abstractmethod
    async def get_by_login(self, login: str) -> Editor | None:
        raise NotImplementedError
