import os
from typing import Any, Dict, List, Optional

import httpx


class DiscussionRestClient:
    def __init__(self) -> None:
        self.base_url = os.getenv("DISCUSSION_BASE_URL", "http://localhost:24130/api/v1.0")
        self._client: Optional[httpx.AsyncClient] = None

    async def _get_client(self) -> httpx.AsyncClient:
        if self._client is None:
            self._client = httpx.AsyncClient(base_url=self.base_url, timeout=2.0)
        return self._client

    async def close(self) -> None:
        if self._client is not None:
            await self._client.aclose()
            self._client = None

    async def create_notice(self, payload: Dict[str, Any]) -> Dict[str, Any]:
        c = await self._get_client()
        r = await c.post("/notices", json=payload)
        if r.status_code >= 400:
            return {"ok": False, "error": {"status": r.status_code, "message": r.text, "errorCode": f"{r.status_code}01"}}
        return {"ok": True, "data": r.json()}

    async def get_all(self, page: int) -> Dict[str, Any]:
        c = await self._get_client()
        r = await c.get("/notices", params={"page": page})
        if r.status_code >= 400:
            return {"ok": False, "error": {"status": r.status_code, "message": r.text, "errorCode": f"{r.status_code}02"}}
        return {"ok": True, "data": r.json()}

    async def get_by_id(self, id: int) -> Dict[str, Any]:
        c = await self._get_client()
        r = await c.get(f"/notices/{id}")
        if r.status_code >= 400:
            return {"ok": False, "error": {"status": r.status_code, "message": r.text, "errorCode": f"{r.status_code}03"}}
        return {"ok": True, "data": r.json()}

    async def update(self, id: int, payload: Dict[str, Any]) -> Dict[str, Any]:
        c = await self._get_client()
        r = await c.put(f"/notices/{id}", json=payload)
        if r.status_code >= 400:
            return {"ok": False, "error": {"status": r.status_code, "message": r.text, "errorCode": f"{r.status_code}04"}}
        return {"ok": True, "data": r.json()}

    async def delete(self, id: int) -> Dict[str, Any]:
        c = await self._get_client()
        r = await c.delete(f"/notices/{id}")
        if r.status_code >= 400:
            return {"ok": False, "error": {"status": r.status_code, "message": r.text, "errorCode": f"{r.status_code}05"}}
        return {"ok": True, "data": None}

