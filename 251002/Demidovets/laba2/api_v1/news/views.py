from fastapi import APIRouter, status, HTTPException, Depends
from sqlalchemy.ext.asyncio import AsyncSession
from loguru import logger

from db_helper import db_helper

from .schemas import News, NewsID
import api_v1.news.crud as crud

from .helpers import news_to_bd, bd_to_id, check_title, check_editor


logger.add(
    sink="RV2Lab.log",
    mode="w",
    encoding="utf-8",
    format="{level} {comment}",
)

router = APIRouter(prefix="/news")


costyl_id = 0


@router.get("/{get_id}", status_code=status.HTTP_200_OK, response_model=NewsID)
async def news_by_id(
    get_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"GET definite News with id: {get_id}")
    news = await crud.get_news(session=session, news_id=get_id)
    if not news:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such News"
        )
    return bd_to_id(news)


@router.get("", status_code=status.HTTP_200_OK, response_model=NewsID)
async def news(session: AsyncSession = Depends(db_helper.session_dependency)):
    logger.info("GET Sticker")
    global costyl_id

    news = await crud.get_news(session=session, news_id=costyl_id)
    if not news:
        return {
            "id": 0,
            "editorId": 0,
            "title": "sdsds",
            "content": "dsdsds",
        }
    return bd_to_id(news)


@router.post("", status_code=status.HTTP_201_CREATED, response_model=NewsID)
async def create_news(
    news_info: News, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"POST News with body: {news_info.model_dump()}")

    await check_editor(
        editor_id=news_info.editorId,
        session=session,
    )

    await check_title(session=session, title=news_info.title)

    news_info = news_to_bd(news_info)
    news = await crud.create_news(session=session, news_info=news_info)

    if not news:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Incorrect data"
        )

    global costyl_id
    costyl_id = news.id

    return bd_to_id(news=news)


@router.delete("/{delete_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_news(
    delete_id: int, session: AsyncSession = Depends(db_helper.session_dependency)
):

    logger.info(f"DELETE News with ID: {delete_id}")
    delete_state = await crud.delete_news(news_id=delete_id, session=session)
    if not delete_state:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="No such News"
        )
    return


@router.put("", status_code=status.HTTP_200_OK, response_model=NewsID)
async def put_news(
    news_info: NewsID, session: AsyncSession = Depends(db_helper.session_dependency)
):
    logger.info(f"PUT News with body: {news_info.model_dump()}")
    news = await crud.put_news(news_info=news_info, session=session)
    if not news:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail="Invlaid PUT data"
        )
    return news
