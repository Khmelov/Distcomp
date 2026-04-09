from typing import Annotated

from fastapi import Depends

from src.services import EditorService


def get_editor_service():
    return EditorService()


type EditorServiceDep = Annotated[EditorService, Depends(get_editor_service)]
