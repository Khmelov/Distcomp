from sqlalchemy import Result, select
from .schemas import Editor as EditorIN, EditorID
from sqlalchemy.ext.asyncio import AsyncSession
from models import Editor


async def create_editor(editor_info: EditorIN, session: AsyncSession):
    editor = Editor(**editor_info.model_dump())
    session.add(editor)
    await session.commit()
    return editor


async def get_editor(session: AsyncSession, editor_id: int):
    stat = select(Editor).where(Editor.id == editor_id)
    result: Result = await session.execute(stat)
    # editors: Sequence = result.scalars().all()
    editor: Editor | None = result.scalar_one_or_none()

    return editor


async def get_editor_by_login(session: AsyncSession, editor_login: str):
    stat = select(Editor).where(Editor.login == editor_login)
    result: Result = await session.execute(stat)
    # editors: Sequence = result.scalars().all()
    editor: Editor | None = result.scalar_one_or_none()
    return editor


async def delete_editor(editor_id: int, session: AsyncSession):
    editor = await get_editor(editor_id=editor_id, session=session)
    if not editor:
        return False
    await session.delete(editor)
    await session.commit()
    return True


async def put_editor(editor_info: EditorID, session: AsyncSession):
    editor_id = editor_info.id
    editor_update = EditorIN(**editor_info.model_dump())
    editor = await get_editor(editor_id=editor_id, session=session)
    if not editor:
        return False

    for name, value in editor_update.model_dump().items():
        setattr(editor, name, value)
    await session.commit()
    return editor
