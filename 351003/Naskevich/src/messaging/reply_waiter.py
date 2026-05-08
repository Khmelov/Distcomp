import asyncio

from src.messaging.post_messages import PostReplyMessage


class ReplyWaiter:
    def __init__(self) -> None:
        self._pending: dict[str, asyncio.Future[PostReplyMessage]] = {}

    def register(self, correlation_id: str) -> asyncio.Future[PostReplyMessage]:
        loop = asyncio.get_running_loop()
        fut: asyncio.Future[PostReplyMessage] = loop.create_future()
        self._pending[correlation_id] = fut
        return fut

    def resolve(self, correlation_id: str, reply: PostReplyMessage) -> None:
        fut = self._pending.pop(correlation_id, None)
        if fut is not None and not fut.done():
            fut.set_result(reply)

    def cancel(self, correlation_id: str) -> None:
        self._pending.pop(correlation_id, None)


post_reply_waiter = ReplyWaiter()
