from fastapi import APIRouter, HTTPException, status
from loguru import logger
from .schemas import News, NewsID
from api_v1.util import clear_storage


logger.add(
        sink = "RV1Lab.log",
        mode="w",
        encoding="utf-8",
        format="{time} {level} {comment}",)

router = APIRouter()
prefix = "/news"

current_news = {
    "id": 0,
    "editorId": 0,
    "title": "",
    "content": "",
}



@router.get(prefix + "/{get_id}",
            status_code=status.HTTP_200_OK,
            response_model=NewsID)
async def news_by_id(
    get_id: int
):
    global current_news
    logger.info(f"GET news by id {get_id}")
    if get_id != current_news["id"]:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such news"
        )
    return NewsID.model_validate(current_news)


@router.get(prefix,
            status_code=status.HTTP_200_OK,
            response_model=NewsID)
async def news():
    global current_news
    logger.info("GET news")
    return NewsID.model_validate(current_news)


@router.post(prefix,
            status_code= status.HTTP_201_CREATED,
            response_model=NewsID)
async def create_news(
    news: News
):
    global current_news
    logger.info(f"POST editor with body: {news.model_dump()}")
    current_news = {"id":0, **news.model_dump() }
    return NewsID.model_validate(current_news)


@router.delete(prefix + "/{delete_id}",
               status_code=status.HTTP_204_NO_CONTENT)
async def delete_news(
    delete_id: int
):
    global current_news
    logger.info(f"DELETE editor with ID: {delete_id}")
    if delete_id != current_news["id"]:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="No such news"
        )
    current_news = clear_storage(current_news)
    current_news["editorId"] = 100000
    return 


@router.put(prefix,
            status_code=status.HTTP_200_OK,
            response_model=NewsID)
async def put_news(
    news: NewsID
):
    global current_news
    logger.info(f"PUT editor with body: {news.model_dump()}")
    if news.title == "x":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Invlaid PUT data"
        )
    current_news = {**news.model_dump()}
    return news

