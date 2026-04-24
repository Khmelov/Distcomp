from src.services.base import BaseCRUDService
from src.models import Editor
from src.schemas.dto import EditorRequestTo, EditorResponseTo


class EditorService(BaseCRUDService[Editor, EditorRequestTo, EditorRequestTo, EditorResponseTo]):
    def __init__(self):
        super().__init__(Editor, EditorResponseTo)
