from .schemas import News as NewsIN, NewsBD, NewsID
from models import News
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import HTTPException, status
import api_v1.news.crud as crud
from api_v1.editors.crud import get_editor


async def check_title(
    title: str,
    session: AsyncSession
):
    """
    Title must be unique 
    """
    title_exists = await crud.get_news_by_title(
        news_title=title,
        session=session
    )
    if title_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Title already exists")
    

async def check_editor(
        editor_id: int,
        session: AsyncSession
):
    """
    We can't connect News with defunct Editor
    """
    editor_exists = await get_editor(editor_id=editor_id, session=session)
    if not editor_exists:
        raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Editor doesn't exist")

def news_to_bd(
        news: NewsIN
) -> NewsBD:
    return NewsBD(
        content= news.content,
        editor_id= news.editorId,
        title= news.title,
    )


def bd_to_id(
        news: News
) -> NewsID:
    return NewsID(
        content=news.content,
        editorId=news.editor_id,
        id=news.id,
        title=news.title,
    )