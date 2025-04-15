from abc import ABC, abstractmethod
from sqlalchemy.future import select
from sqlalchemy.exc import NoResultFound, IntegrityError
from src.db.db import async_session_maker


class NotFoundError(Exception):
    pass

class AbstractRepository(ABC):

    @abstractmethod
    def create(self, item):
        pass


    @abstractmethod
    def get_all(self):
        pass

    @abstractmethod
    def get_by_id(self, item_id: int):
        pass

    @abstractmethod
    def update(self, item):
        pass

    @abstractmethod
    def delete(self, item_id: int):
        pass

class InMemoryRepository(AbstractRepository):
    def __init__(self):
        self._storage = {}
        self._id_counter = 1

    def create(self, item):
        item.id = self._id_counter
        self._id_counter += 1
        self._storage[item.id] = item
        return item

    def get_all(self):
        return list(self._storage.values())

    def get_by_id(self, item_id: int):
        if item_id not in self._storage:
            raise NotFoundError(f"Item with id {item_id} not found")
        return self._storage[item_id]

    def update(self, item):
        if item.id not in self._storage:
            raise NotFoundError(f"Item with id {item.id} not found")
        self._storage[item.id] = item
        return item


    def delete(self, item_id: int):
        if item_id not in self._storage:
            raise NotFoundError(f"Item with id {item_id} not found")
        self._storage.pop(item_id)


class SQLAlchemyRepository(AbstractRepository):
    model = None

    async def create(self, item):
        async with async_session_maker() as session:
            try:
                session.add(item)
                await session.commit()
                await session.refresh(item)
                return item
            except IntegrityError:
                await session.rollback()
                raise ValueError("Integrity error")

    async def get_all(self):
        async with async_session_maker() as session:
            items = await session.run_sync(lambda s: s.query(self.model).all())
            return items

    async def get_by_id(self, item_id: int):
        async with async_session_maker() as session:
            item = await session.get(self.model, item_id)
            if item is None:
                raise NotFoundError(f"Item with id {item_id} not found")
            return item

    async def update(self, item):
        async with async_session_maker() as session:
            db_item = await session.merge(item)
            await session.commit()
            await session.refresh(db_item)
            return db_item

    async def delete(self, item_id: int):
        async with async_session_maker() as session:
            db_item = await session.get(self.model, item_id)
            if not db_item:
                raise NotFoundError(f"Item with id {item_id} not found")
            await session.delete(db_item)
            await session.commit()
