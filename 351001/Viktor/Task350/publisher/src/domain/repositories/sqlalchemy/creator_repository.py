from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from typing import Optional, List
from Task350.publisher.src.domain.models import Creator
from Task350.publisher.src.domain.repositories.interfaces import Repository
from Task350.publisher.src.infrastructure.models.creator_model import CreatorModel
from sqlalchemy import delete
from Task350.publisher.src.infrastructure.models.tweet_model import TweetModel

class SQLAlchemyCreatorRepository(Repository[Creator]):
    def __init__(self, session: AsyncSession):
        self.session = session

    async def get_one(self, entity_id: int) -> Creator:
        try:
            model = await self.session.get(CreatorModel, entity_id)
        except Exception:
            # Любая ошибка при получении записи считаем, что запись не найдена
            raise KeyError
        if not model:
            raise KeyError
        return self._to_domain(model)

    async def get_all(
        self,
        page: int = 1,
        size: int = 20,
        sort: str = "id",
        login: Optional[str] = None,
        firstname: Optional[str] = None,
        lastname: Optional[str] = None
    ) -> List[Creator]:
        query = select(CreatorModel)
        if login:
            query = query.where(CreatorModel.login.ilike(f"%{login}%"))
        if firstname:
            query = query.where(CreatorModel.firstname.ilike(f"%{firstname}%"))
        if lastname:
            query = query.where(CreatorModel.lastname.ilike(f"%{lastname}%"))

        # Безопасная сортировка
        allowed_sort_fields = ['id', 'login', 'firstname', 'lastname']
        sort_field = sort.lstrip('-')
        if sort_field not in allowed_sort_fields:
            sort_field = 'id'
        order_column = getattr(CreatorModel, sort_field)
        if sort.startswith('-'):
            order_column = order_column.desc()
        query = query.order_by(order_column)

        query = query.offset((page - 1) * size).limit(size)
        result = await self.session.execute(query)
        return [self._to_domain(row) for row in result.scalars()]

    async def create(self, entity: Creator) -> Creator:
        model = CreatorModel(
            login=entity.login,
            password=entity.password,
            firstname=entity.firstname,
            lastname=entity.lastname
        )
        self.session.add(model)
        await self.session.flush()
        await self.session.refresh(model)
        return self._to_domain(model)

    async def update(self, entity: Creator) -> Creator:
        model = await self.session.get(CreatorModel, entity.id)
        if not model:
            raise KeyError
        model.login = entity.login
        model.password = entity.password
        model.firstname = entity.firstname
        model.lastname = entity.lastname
        await self.session.flush()
        return self._to_domain(model)

    async def delete(self, entity_id: int) -> None:
        model = await self.session.get(CreatorModel, entity_id)
        if not model:
            raise KeyError

        # Явно удаляем все твиты, принадлежащие этому создателю
        await self.session.execute(
            delete(TweetModel).where(TweetModel.creator_id == entity_id)
        )

        await self.session.delete(model)
        await self.session.flush()

    def _to_domain(self, model: CreatorModel) -> Creator:
        return Creator(
            id=model.id,
            login=model.login,
            password=model.password,
            firstname=model.firstname,
            lastname=model.lastname
        )