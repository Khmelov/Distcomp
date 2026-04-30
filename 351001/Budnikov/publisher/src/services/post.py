import os
import httpx
from fastapi.encoders import jsonable_encoder
from src.schemas.dto import PostRequestTo, PostResponseTo
from src.core.exceptions import BaseAppException
from src.models import Issue


DISCUSSION_URL = os.getenv("DISCUSSION_SERVICE_URL", "http://127.0.0.1:24130/api/v1.0/posts")


class PostService:
    async def get_all(self) -> list[PostResponseTo]:
        try:
            async with httpx.AsyncClient() as client:
                resp = await client.get(DISCUSSION_URL)
                resp.raise_for_status()
                return [PostResponseTo(**p) for p in resp.json()]
        except httpx.ConnectError:
            raise BaseAppException(500, "50000", "Микросервис discussion недоступен. Запустите его на порту 24130.")

    async def get_by_id(self, obj_id: int) -> PostResponseTo:
        try:
            async with httpx.AsyncClient() as client:
                resp = await client.get(f"{DISCUSSION_URL}/{obj_id}")
                if resp.status_code == 404:
                    raise BaseAppException(404, "40401", f"Post with id {obj_id} not found")
                resp.raise_for_status()
                return PostResponseTo(**resp.json())
        except httpx.ConnectError:
            raise BaseAppException(500, "50000", "Микросервис discussion недоступен.")

    async def create(self, create_dto: PostRequestTo) -> PostResponseTo:
        issue_id = getattr(create_dto, "issue_id", getattr(create_dto, "issueId", None))

        issue_exists = await Issue.filter(id=issue_id).exists()
        if not issue_exists:
            raise BaseAppException(400, "40004", f"Issue with id {issue_id} not found")

        payload = jsonable_encoder(create_dto)

        try:
            async with httpx.AsyncClient() as client:
                resp = await client.post(DISCUSSION_URL, json=payload)
                resp.raise_for_status()
                return PostResponseTo(**resp.json())
        except httpx.ConnectError:
            raise BaseAppException(500, "50000", "Микросервис discussion недоступен.")

    async def update(self, obj_id: int, update_dto: PostRequestTo) -> PostResponseTo:
        payload = jsonable_encoder(update_dto)
        try:
            async with httpx.AsyncClient() as client:
                resp = await client.put(f"{DISCUSSION_URL}/{obj_id}", json=payload)
                if resp.status_code == 404:
                    raise BaseAppException(404, "40402", "Post not found")
                resp.raise_for_status()
                return PostResponseTo(**resp.json())
        except httpx.ConnectError:
            raise BaseAppException(500, "50000", "discussion is not available.")

    async def delete(self, obj_id: int) -> None:
        try:
            async with httpx.AsyncClient() as client:
                resp = await client.delete(f"{DISCUSSION_URL}/{obj_id}")
                if resp.status_code == 404:
                    raise BaseAppException(404, "40403", "Post not found")
                resp.raise_for_status()
        except httpx.ConnectError:
            raise BaseAppException(500, "50000", "discussion is not available.")
