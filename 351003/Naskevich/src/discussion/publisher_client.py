import httpx

from src.exceptions import EntityNotFoundException


class PublisherClient:
    def __init__(self, base_url: str) -> None:
        self._base = base_url.rstrip("/")

    async def ensure_tweet_exists(self, tweet_id: int) -> None:
        url = f"{self._base}/tweets/{tweet_id}"
        async with httpx.AsyncClient(timeout=10.0) as client:
            response = await client.get(url)
        if response.status_code == 404:
            raise EntityNotFoundException("Tweet", tweet_id)
        response.raise_for_status()
