from fastapi import APIRouter, status
from src.schemas.dto import PostRequestTo, PostResponseTo
from src.dependencies.services import PostServiceDep


router = APIRouter(prefix="/posts")


@router.post(path="", response_model=PostResponseTo, status_code=status.HTTP_201_CREATED)
async def create_post(post_in: PostRequestTo, post_service: PostServiceDep):
    return await post_service.create(post_in)


@router.get(path="", response_model=list[PostResponseTo], status_code=status.HTTP_200_OK)
async def get_posts(post_service: PostServiceDep):
    return await post_service.get_all()


@router.get(path="/{id}", response_model=PostResponseTo, status_code=status.HTTP_200_OK)
async def get_post(id: int, post_service: PostServiceDep):
    return await post_service.get_by_id(id)


@router.put(path="/{id}", response_model=PostResponseTo, status_code=status.HTTP_200_OK)
async def update_post(id: int, post_in: PostRequestTo, post_service: PostServiceDep):
    return await post_service.update(id, post_in)


@router.delete(path="/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_post(id: int, post_service: PostServiceDep):
    await post_service.delete(id)
