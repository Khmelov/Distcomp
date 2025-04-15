from src.models.models import Creator
from src.utils.repository import InMemoryRepository, SQLAlchemyRepository


class CreatorsRepository(SQLAlchemyRepository):
    model = Creator