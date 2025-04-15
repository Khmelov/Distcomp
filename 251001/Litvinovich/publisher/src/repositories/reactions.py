from src.models.models import Reaction
from src.utils.repository import InMemoryRepository, SQLAlchemyRepository


class ReactionsRepository(SQLAlchemyRepository):
    model = Reaction