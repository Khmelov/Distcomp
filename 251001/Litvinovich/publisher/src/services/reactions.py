import json
from fastapi import HTTPException
from src.schemas.reactions import ReactionResponseTo, ReactionRequestToAdd, ReactionRequestToUpdate
from src.utils.kafka_client import send_kafka_request
import redis.asyncio as redis

global_id_counter = 0

CACHE_TTL = 300

class ReactionsService:
    def __init__(self, redis_client: redis.Redis):
        self.redis = redis_client

    async def create_reaction(self, reaction: ReactionRequestToAdd) -> ReactionResponseTo:
        global global_id_counter
        global_id_counter += 1
        payload = {
            "id": global_id_counter,
            "storyId": reaction.storyId,
            "content": reaction.content,
        }
        result = await send_kafka_request("POST", payload)
        if result.get("error"):
            raise HTTPException(status_code=400, detail=result["error"])

        await self.redis.delete("reactions")
        return ReactionResponseTo(**result)

    async def get_reactions(self) -> list[ReactionResponseTo]:
        cached = await self.redis.get("reactions")
        if cached:
            reactions = json.loads(cached)
            return [ReactionResponseTo(**r) for r in reactions]

        result = await send_kafka_request("GET", {})
        if isinstance(result, list):
            await self.redis.set("reactions", json.dumps(result), ex=CACHE_TTL)
            return [ReactionResponseTo(**r) for r in result]

        raise HTTPException(status_code=400, detail="Ошибка получения списка реакций")

    async def get_reaction_by_id(self, reaction_id: int) -> ReactionResponseTo:
        key = f"reaction:{reaction_id}"
        cached = await self.redis.get(key)
        if cached:
            data = json.loads(cached)
            return ReactionResponseTo(**data)

        payload = {"id": reaction_id}
        result = await send_kafka_request("GET_BY_ID", payload)
        if result.get("error"):
            raise HTTPException(status_code=404, detail=result["error"])

        await self.redis.set(key, json.dumps(result), ex=CACHE_TTL)
        return ReactionResponseTo(**result)

    async def update_reaction(self, reaction: ReactionRequestToUpdate) -> ReactionResponseTo:
        payload = {
            "id": reaction.id,
            "storyId": reaction.storyId,
            "content": reaction.content,
        }
        result = await send_kafka_request("PUT", payload)
        if result.get("error"):
            raise HTTPException(status_code=400, detail=result["error"])

        await self.redis.delete(f"reaction:{reaction.id}")
        await self.redis.delete("reactions")
        return ReactionResponseTo(**result)

    async def delete_reaction(self, reaction_id: int):
        payload = {"id": reaction_id}
        result = await send_kafka_request("DELETE", payload)
        if result.get("error"):
            raise HTTPException(status_code=404, detail=result["error"])

        await self.redis.delete(f"reaction:{reaction_id}")
        await self.redis.delete("reactions")
        return result
