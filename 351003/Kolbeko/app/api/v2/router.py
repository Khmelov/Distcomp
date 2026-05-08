from fastapi import APIRouter, Depends, status
from app.api.v1.endpoints import authors, tweets, labels, notices
from app.api.v2.endpoints import auth
from app.core.auth import get_current_user, require_admin

api_router = APIRouter()

api_router.include_router(auth.router, tags=["Auth v2"])

api_router.post(
    "/authors", 
    status_code=status.HTTP_201_CREATED, 
    tags=["Authors v2"]
)(authors.create)

read_params = {"dependencies": [Depends(get_current_user)], "tags": ["v2 Read"]}
api_router.get("/authors", **read_params)(authors.get_all)
api_router.get("/authors/{id}", **read_params)(authors.get_by_id)
api_router.get("/tweets", **read_params)(tweets.get_all)
api_router.get("/tweets/{id}", **read_params)(tweets.get_by_id)
api_router.get("/labels", **read_params)(labels.get_all)
api_router.get("/labels/{id}", **read_params)(labels.get_by_id)
api_router.get("/notices", **read_params)(notices.get_all)
api_router.get("/notices/{id}", **read_params)(notices.get_by_id)

admin_params = {"dependencies": [Depends(require_admin)], "tags": ["v2 Admin Write"]}

api_router.put("/authors/{id}", **admin_params)(authors.update)
api_router.delete(
    "/authors/{id}", 
    status_code=status.HTTP_204_NO_CONTENT, 
    **admin_params
)(authors.delete)

api_router.post(
    "/tweets", 
    status_code=status.HTTP_201_CREATED, 
    **admin_params
)(tweets.create)
api_router.put("/tweets/{id}", **admin_params)(tweets.update)
api_router.delete(
    "/tweets/{id}", 
    status_code=status.HTTP_204_NO_CONTENT, 
    **admin_params
)(tweets.delete)

api_router.post(
    "/labels", 
    status_code=status.HTTP_201_CREATED, 
    **admin_params
)(labels.create)
api_router.put("/labels/{id}", **admin_params)(labels.update)
api_router.delete(
    "/labels/{id}", 
    status_code=status.HTTP_204_NO_CONTENT, 
    **admin_params
)(labels.delete)

api_router.post(
    "/notices", 
    status_code=status.HTTP_201_CREATED, 
    **admin_params
)(notices.create)
api_router.put("/notices/{id}", **admin_params)(notices.update)
api_router.delete(
    "/notices/{id}", 
    status_code=status.HTTP_204_NO_CONTENT, 
    **admin_params
)(notices.delete)