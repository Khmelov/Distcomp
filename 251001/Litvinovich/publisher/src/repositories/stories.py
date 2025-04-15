from src.models.models import Story
from src.utils.repository import InMemoryRepository, SQLAlchemyRepository


class StoriesRepository(SQLAlchemyRepository):
    model = Story