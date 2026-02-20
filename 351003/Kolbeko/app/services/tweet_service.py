from datetime import datetime
from app.repository.memory import tweet_repo, author_repo
from app.schemas.tweet import TweetRequestTo, TweetResponseTo
from app.core.exceptions import AppException

class TweetService:
    def create(self, dto: TweetRequestTo) -> TweetResponseTo:
        if not author_repo.find_by_id(dto.authorId):
            raise AppException(400, "Author not found", 4)
        now = datetime.now().isoformat()
        data = dto.model_dump()
        data.update({"created": now, "modified": now})
        return TweetResponseTo(**tweet_repo.save(data))

    def get_all(self):
        return [TweetResponseTo(**t) for t in tweet_repo.find_all()]

    def get_by_id(self, id: int):
        res = tweet_repo.find_by_id(id)
        if not res: raise AppException(404, "Tweet not found", 5)
        return TweetResponseTo(**res)

    def update(self, id: int, dto: TweetRequestTo) -> TweetResponseTo:
        if not author_repo.find_by_id(dto.authorId):
            raise AppException(400, "Author not found", 7)
        
        existing = tweet_repo.find_by_id(id)
        now = datetime.now().isoformat()
        data = dto.model_dump()
        data["id"] = id
        
        if existing:
            data["created"] = existing.get("created", now)
            data["modified"] = now
        else:
            data["created"] = now
            data["modified"] = now
            
        return TweetResponseTo(**tweet_repo.save(data))

    def delete(self, id: int):
        if not tweet_repo.delete(id):
            raise AppException(404, "Tweet not found", 8)