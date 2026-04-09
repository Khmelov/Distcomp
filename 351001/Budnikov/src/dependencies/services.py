from typing import Annotated
from fastapi import Depends

from src.services import EditorService, LabelService, PostService, IssueService


def get_editor_service():
    return EditorService()


def get_label_service():
    return LabelService()


def get_post_service():
    return PostService()


def get_issue_service():
    return IssueService()


type EditorServiceDep = Annotated[EditorService, Depends(get_editor_service)]
type LabelServiceDep = Annotated[LabelService, Depends(get_label_service)]
type PostServiceDep = Annotated[PostService, Depends(get_post_service)]
type IssueServiceDep = Annotated[IssueService, Depends(get_issue_service)]
