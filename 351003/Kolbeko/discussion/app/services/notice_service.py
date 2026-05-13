from discussion.app.core.exceptions import AppException
from discussion.app.repository.notice_repo import NoticeRepository
from discussion.app.services.moderation import moderate


class NoticeService:
    def __init__(self) -> None:
        self.repo = NoticeRepository()

    async def create(self, dto: dict) -> dict:
        # moderation changes state from PENDING -> APPROVE/DECLINE
        state = moderate(dto["content"])
        data = {
            "id": int(dto["id"]),
            "tweet_id": int(dto["tweetId"]),
            "content": dto["content"],
            "state": state,
        }
        return await self.repo.create(data)

    async def get_all(self, page: int = 1) -> list[dict]:
        return await self.repo.get_all(page=page)

    async def get_by_id(self, id: int) -> dict:
        res = await self.repo.get_by_id(id)
        if not res:
            raise AppException(404, "Notice not found", 13)
        return res

    async def update(self, id: int, dto: dict) -> dict:
        updated = await self.repo.update(id=int(id), tweet_id=int(dto["tweetId"]), content=dto["content"])
        if not updated:
            raise AppException(404, "Notice not found", 15)
        return updated

    async def delete(self, id: int) -> None:
        if not await self.repo.delete(int(id)):
            raise AppException(404, "Notice not found", 16)

