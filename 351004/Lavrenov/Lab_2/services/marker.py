from models.marker import Marker
from dto.requests import MarkerRequestTo
from dto.responses import MarkerResponseTo
from .base import BaseService


class MarkerService(BaseService[Marker, MarkerRequestTo, MarkerResponseTo]):
    def _to_entity(self, request: MarkerRequestTo) -> Marker:
        return Marker(name=request.name)

    def _to_response(self, entity: Marker) -> MarkerResponseTo:
        return MarkerResponseTo(id=entity.id, name=entity.name)
