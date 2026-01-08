from fastapi import APIRouter, status, HTTPException
from .schemas import Comment, CommentID
from loguru import logger
from api_v1.util import clear_storage

logger.add(
        sink = "app.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {comments}",)

router = APIRouter()
prefix = "/comments"


current_comment = {
    "id": 0,
    "issueId": 0,
    "content": "",
}


@router.get(prefix + "/{get_id}",
            status_code=status.HTTP_200_OK,
            response_model=CommentID)
async def comment_by_id(
    get_id: int
):
    global current_comment
    logger.info(f"GET comment by id: {get_id}")
    if current_comment["id"] != get_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such comment"
        )
    return CommentID.model_validate(current_comment)


@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=CommentID)
async def comment():
    global current_comment
    logger.info("GET comment")
    return CommentID.model_validate(current_comment)



@router.post(prefix, 
             status_code=status.HTTP_201_CREATED,
             response_model=CommentID)
async def create_comment(
    comment: Comment
):
    global current_comment
    logger.info(f"POST comment with body: {comment.model_dump()}")
    current_comment = {"id":0, **comment.model_dump() }

    return CommentID.model_validate(current_comment)




@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_comment(
    delete_id: int
):
    global current_comment
    logger.info(f"DELETE comment with ID: {delete_id}")
    if current_comment["id"] != delete_id:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such comment")

    
    current_comment = clear_storage(current_comment)
    current_comment["issueId"] = 100000
    return 



@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=CommentID)
async def put_writer(
    comment: CommentID
):
    global current_comment
    logger.info(f"PUT comment with body: {comment.model_dump()}")
    # if comment. == 'x':
    #         raise HTTPException(
    #         status_code=status.HTTP_400_BAD_REQUEST,
    #         detail="Invlaid PUT data")

    current_comment = {**comment.model_dump()}
    return comment