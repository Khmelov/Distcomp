# publisher/services/comment_service.py
import httpx

from errors import AppError

DISCUSSION_URL = "http://localhost:24130/api/v1.0/comments"

class CommentService:
    def create(self, dto):
        r = httpx.post(DISCUSSION_URL, json={"tweetId": dto.tweet_id, "content": dto.content})
        r.raise_for_status()
        return r.json()

    def get_all(self):
        return httpx.get(DISCUSSION_URL).json()

    def get_by_id(self, id):
        r = httpx.get(f"{DISCUSSION_URL}/{id}")
        if r.status_code == 404:
            raise AppError(status_code=404, message="Comment not found", error_code=40404)
        return r.json()

    def delete(self, id):
        r = httpx.delete(f"{DISCUSSION_URL}/{id}")
        if r.status_code == 404:
            raise AppError(status_code=404, message="Comment not found", error_code=40404)