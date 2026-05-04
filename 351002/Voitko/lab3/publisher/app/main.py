"""
Запуск из IDE: корень проекта — родитель каталога app.
Hypercorn: HTTP/2 через TLS (ALPN), если заданы SSL_CERTFILE/SSL_KEYFILE или dev-cert.pem/dev-key.pem.
"""
import os
import sys
from pathlib import Path

_ROOT = Path(__file__).resolve().parent.parent

os.chdir(_ROOT)
_prev = os.environ.get("PYTHONPATH", "")
os.environ["PYTHONPATH"] = (
    str(_ROOT) if not _prev else str(_ROOT) + os.pathsep + _prev
)
if str(_ROOT) not in sys.path:
    sys.path.insert(0, str(_ROOT))

if __name__ == "__main__":
    from hypercorn.config import Config
    from hypercorn.run import run

    cfg = Config()
    cfg.application_path = "main:app"
    cfg.bind = ["0.0.0.0:24110"]
    ec, ek = os.environ.get("SSL_CERTFILE"), os.environ.get("SSL_KEYFILE")
    if ec and ek:
        cfg.certfile, cfg.keyfile = ec, ek
    else:
        c, k = _ROOT / "dev-cert.pem", _ROOT / "dev-key.pem"
        if c.is_file() and k.is_file():
            cfg.certfile, cfg.keyfile = str(c), str(k)
    cfg.use_reloader = os.environ.get("HYPERCORN_RELOAD", "1") == "1"
    cfg.workers = 1
    run(cfg)
