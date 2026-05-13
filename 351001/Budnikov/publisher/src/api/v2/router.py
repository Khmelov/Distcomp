from fastapi import APIRouter
from src.api.v2.endpoints.auth import router as auth_router
from src.api.v2.endpoints.editors import router as editor_router
from src.api.v2.endpoints.issues import router as issue_router
from src.api.v2.endpoints.labels import router as label_router
from src.api.v2.endpoints.posts import router as post_router


api_router_v2 = APIRouter(prefix="/v2.0")


api_router_v2.include_router(auth_router, tags=["V2 Auth"])
api_router_v2.include_router(editor_router, tags=["V2 Editors"])
api_router_v2.include_router(issue_router, tags=["V2 Issues"])
api_router_v2.include_router(label_router, tags=["V2 Labels"])
api_router_v2.include_router(post_router, tags=["V2 Posts"])