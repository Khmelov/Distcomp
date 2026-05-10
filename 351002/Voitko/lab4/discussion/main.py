import os
from contextlib import asynccontextmanager
from pathlib import Path

from fastapi import FastAPI

from discussion_mod.api.v1.router import router_v1
from discussion_mod.core.database import close_db, init_db
from discussion_mod.core.handlers import register_error_handlers
from discussion_mod.core.kafka_worker import start_kafka_worker, stop_kafka_worker


@asynccontextmanager
async def lifespan(_: FastAPI):
    init_db()
    start_kafka_worker()
    yield
    stop_kafka_worker()
    close_db()


app = FastAPI(title="DistComp discussion (ask330)", version="1.0", lifespan=lifespan)

register_error_handlers(app)
app.include_router(router_v1, prefix="/api")


if __name__ == "__main__":
    from hypercorn.config import Config
    from hypercorn.run import run

    _root = Path(__file__).resolve().parent
    os.chdir(_root)
    cfg = Config()
    cfg.application_path = "main:app"
    cfg.bind = ["0.0.0.0:24130"]
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
