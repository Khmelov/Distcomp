from src.models.models import Marker
from src.utils.repository import SQLAlchemyRepository


class MarkersRepository(SQLAlchemyRepository):
    model = Marker