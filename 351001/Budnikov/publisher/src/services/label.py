from src.services.base import BaseCRUDService
from src.models import Label
from src.schemas.dto import LabelRequestTo, LabelResponseTo


class LabelService(BaseCRUDService[Label, LabelRequestTo, LabelRequestTo, LabelResponseTo]):
    def __init__(self):
        super().__init__(Label, LabelResponseTo)
