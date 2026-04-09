from src.services.base import BaseCRUDService
from src.models import Post, Issue
from src.schemas.dto import PostRequestTo, PostResponseTo
from src.core.exceptions import BaseAppException


class PostService(BaseCRUDService[Post, PostRequestTo, PostRequestTo, PostResponseTo]):
    def __init__(self):
        super().__init__(Post, PostResponseTo)

    async def create(self, create_dto: PostRequestTo) -> PostResponseTo:
        issue_exists = await Issue.filter(id=create_dto.issue_id).exists()
        if not issue_exists:
            raise BaseAppException(400, "40004", f"Issue with id {create_dto.issue_id} not found")

        return await super().create(create_dto)
