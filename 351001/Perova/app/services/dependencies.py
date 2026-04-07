from sqlalchemy import text

from app.models.issue import Issue
from app.models.notice import Notice
from app.models.sticker import Sticker
from app.models.user import User
from app.repositories import InMemoryRepository
from app.services.issue import IssueService
from app.services.notice import NoticeService
from app.services.sticker import StickerService
from app.services.user import UserService
from app.settings import settings

if settings.storage == "memory":
    user_repository = InMemoryRepository[User]()
    issue_repository = InMemoryRepository[Issue]()
    sticker_repository = InMemoryRepository[Sticker]()
    notice_repository = InMemoryRepository[Notice]()

    def _delete_notices_for_issue_memory(issue_id: int) -> None:
        for n in list(notice_repository.find_all()):
            if n.issueId == issue_id and n.id is not None:
                notice_repository.delete_by_id(n.id)

else:
    from app.db.session import get_session_factory
    from app.repositories.http import HttpNoticeRepository
    from app.repositories.postgres import (
        PostgresIssueRepository,
        PostgresStickerRepository,
        PostgresUserRepository,
    )

    _session_factory = get_session_factory()
    user_repository = PostgresUserRepository(_session_factory)
    issue_repository = PostgresIssueRepository(_session_factory)
    sticker_repository = PostgresStickerRepository(_session_factory)
    notice_repository = HttpNoticeRepository(settings.discussion_base_url)

    def _delete_notices_for_issue_memory(issue_id: int) -> None:
        notice_repository.delete_all_for_issue(issue_id)


user_service = UserService(user_repository)
sticker_service = StickerService(sticker_repository)
issue_service = IssueService(
    issue_repository,
    user_repository,
    sticker_repository,
    delete_notices_for_issue=_delete_notices_for_issue_memory,
)
notice_service = NoticeService(notice_repository, issue_repository)


def reset_storage() -> None:
    if settings.storage == "memory":
        repositories = [user_repository, issue_repository, sticker_repository, notice_repository]
        for repository in repositories:
            repository._items.clear()
            repository._next_id = 1
        return
    from app.db.session import get_session_factory

    sf = get_session_factory()
    with sf() as session:
        session.execute(
            text(
                "TRUNCATE distcomp.tbl_issue_sticker, "
                "distcomp.tbl_issue, distcomp.tbl_user, distcomp.tbl_sticker "
                "RESTART IDENTITY CASCADE"
            )
        )
        session.commit()
