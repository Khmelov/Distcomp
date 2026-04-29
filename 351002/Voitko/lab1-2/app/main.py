"""
Запуск из IDE: D:\\lab1\\app\\main.py
Корень проекта должен быть CWD, иначе uvicorn reload подхватит этот файл как module "main".
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
    import uvicorn

    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=24110,
        reload=True,
        reload_dirs=[str(_ROOT)],
    )
