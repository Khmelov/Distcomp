from consumer import consume
from producer import produce
import asyncio

asyncio.run(produce())
asyncio.run(consume())
