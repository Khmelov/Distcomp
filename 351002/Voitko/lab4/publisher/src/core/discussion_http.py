from __future__ import annotations

from typing import Optional

import httpx

from src.core.settings import settings

_client: Optional[httpx.Client] = None


def get_discussion_client() -> httpx.Client:
    global _client
    if _client is None:
        base = settings.DISCUSSION_BASE_URL.rstrip("/")
        _client = httpx.Client(base_url=base, timeout=30.0)
    return _client


def set_discussion_client(client: Optional[httpx.Client]) -> None:
    """Для тестов: подмена HTTP-клиента (например, ASGITransport к discussion)."""
    global _client
    if _client is not None and client is None:
        try:
            _client.close()
        except Exception:
            pass
    _client = client
