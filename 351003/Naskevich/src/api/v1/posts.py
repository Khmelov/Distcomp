from fastapi import APIRouter
from starlette import status

from src.api.dependencies import PostServiceDep
from src.dto.post import PostRequestTo, PostResponseTo

router = APIRouter(prefix="/posts", tags=["posts"])


@router.get("", response_model=list[PostResponseTo])
async def get_posts(service: PostServiceDep):
    return await service.get_all()


@router.get("/{post_id}", response_model=PostResponseTo)
async def get_post(post_id: int, service: PostServiceDep):
    return await service.get_by_id(post_id)


@router.post("", response_model=PostResponseTo, status_code=status.HTTP_201_CREATED)
async def create_post(data: PostRequestTo, service: PostServiceDep):
    return await service.create(data)


@router.put("/{post_id}", response_model=PostResponseTo)
async def update_post(post_id: int, data: PostRequestTo, service: PostServiceDep):
    return await service.update(post_id, data)


@router.delete("/{post_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_post(post_id: int, service: PostServiceDep):
    await service.delete(post_id)
