from fastapi import FastAPI
from src.api.v1 import router_v1
from src.core.errors import register_error_handlers

app = FastAPI(title="DistComp", version="1.0")
register_error_handlers(app)
app.include_router(router_v1, prefix="/api")

if __name__ == "__main__":
    import hypercorn.asyncio
    import asyncio
    from hypercorn.config import Config

    config = Config()
    config.bind = ["127.0.0.1:24110"]
    config.use_reloader = True
    asyncio.run(hypercorn.asyncio.serve(app, config))