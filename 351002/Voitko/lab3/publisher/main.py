import os
from contextlib import asynccontextmanager

from fastapi import FastAPI

from src.api.v1 import router_v1
from src.core.database import ensure_schema_and_tables
from src.core.errors import register_error_handlers


@asynccontextmanager
async def lifespan(_: FastAPI):
    if os.environ.get("SKIP_DB_INIT") != "1":
        ensure_schema_and_tables()
    yield


app = FastAPI(title="DistComp publisher (ask330 lab3)", version="1.0", lifespan=lifespan)

register_error_handlers(app)

app.include_router(router_v1, prefix="/api")


if __name__ == "__main__":
    from pathlib import Path

    from hypercorn.config import Config
    from hypercorn.run import run

    _root = Path(__file__).resolve().parent
    os.chdir(_root)
    cfg = Config()
    cfg.application_path = "main:app"
    cfg.bind = ["0.0.0.0:24110"]
    ec, ek = os.environ.get("SSL_CERTFILE"), os.environ.get("SSL_KEYFILE")
    if ec and ek:
        cfg.certfile, cfg.keyfile = ec, ek
    else:
        c, k = _root / "dev-cert.pem", _root / "dev-key.pem"
        if c.is_file() and k.is_file():
            cfg.certfile, cfg.keyfile = str(c), str(k)
    cfg.use_reloader = os.environ.get("HYPERCORN_RELOAD", "1") == "1"
    cfg.workers = 1
    run(cfg)
