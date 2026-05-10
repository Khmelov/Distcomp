from fastapi import FastAPI

from src.api.v1 import router_v1
from src.core.errors import register_error_handlers

app = FastAPI(title="DistComp", version="1.0")

# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=[
#         "*",
#     ],
#     allow_credentials=True,
#     allow_methods=["*"],
#     allow_headers=["*"],
# )

register_error_handlers(app)

app.include_router(router_v1, prefix="/api")