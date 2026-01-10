from pathlib import Path
import sys

import uvicorn

# Ensure project root on sys.path when running as a script.
ROOT_DIR = Path(__file__).resolve().parent.parent
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from app.api import app  # noqa: E402


def run() -> None:
    uvicorn.run(app, host="localhost", port=24110, reload=False)


if __name__ == "__main__":
    run()
