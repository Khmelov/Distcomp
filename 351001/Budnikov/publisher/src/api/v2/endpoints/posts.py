from fastapi import APIRouter, status
from src.schemas.dto import PostRequestTo, PostResponseTo
from src.dependencies.services import PostServiceDep
from src.dependencies.auth import CurrentUserDep, verify_permissions


router = APIRouter(prefix="/posts")


@router.post("", response_model=PostResponseTo, status_code=status.HTTP_201_CREATED)
async def create_post(post_in: PostRequestTo, post_service: PostServiceDep, current_user: CurrentUserDep):
    return await post_service.create(post_in)


@router.get("", response_model=list[PostResponseTo], status_code=status.HTTP_200_OK)
async def get_posts(post_service: PostServiceDep, current_user: CurrentUserDep):
    return await post_service.get_all()


@router.get("/{id}", response_model=PostResponseTo, status_code=status.HTTP_200_OK)
async def get_post(id: int, post_service: PostServiceDep, current_user: CurrentUserDep):
    return await post_service.get_by_id(id)


@router.put("/{id}", response_model=PostResponseTo, status_code=status.HTTP_200_OK)
async def update_post(id: int, post_in: PostRequestTo, post_service: PostServiceDep, current_user: CurrentUserDep):
    # Так как у постов нет автора в Кассандре, менять их может только ADMIN
    verify_permissions(current_user)
    return await post_service.update(id, post_in)


@router.delete("/{id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_post(id: int, post_service: PostServiceDep, current_user: CurrentUserDep):
    verify_permissions(current_user)
    await post_service.delete(id)
