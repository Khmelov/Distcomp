from app.repository.memory import notice_repo, tweet_repo
from app.schemas.notice import NoticeRequestTo, NoticeResponseTo
from app.core.exceptions import AppException

class NoticeService:
    def create(self, dto: NoticeRequestTo) -> NoticeResponseTo:
        if not tweet_repo.find_by_id(dto.tweetId):
            raise AppException(400, "Tweet not found", 12)
        return NoticeResponseTo(**notice_repo.save(dto.model_dump()))

    def get_all(self):
        return [NoticeResponseTo(**n) for n in notice_repo.find_all()]

    def get_by_id(self, id: int):
        res = notice_repo.find_by_id(id)
        if not res: raise AppException(404, "Notice not found", 13)
        return NoticeResponseTo(**res)

    def update(self, id: int, dto: NoticeRequestTo) -> NoticeResponseTo:
        if not tweet_repo.find_by_id(dto.tweetId):
            raise AppException(400, "Tweet not found", 15)
        data = dto.model_dump()
        data["id"] = id
        return NoticeResponseTo(**notice_repo.save(data))

    def delete(self, id: int):
        if not notice_repo.delete(id):
            raise AppException(404, "Notice not found", 16)