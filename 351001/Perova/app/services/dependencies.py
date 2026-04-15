from app.models.issue import Issue
from app.models.notice import Notice
from app.models.sticker import Sticker
from app.models.user import User
from app.repositories import InMemoryRepository
from app.services.issue import IssueService
from app.services.notice import NoticeService
from app.services.sticker import StickerService
from app.services.user import UserService

user_repository = InMemoryRepository[User]()
issue_repository = InMemoryRepository[Issue]()
sticker_repository = InMemoryRepository[Sticker]()
notice_repository = InMemoryRepository[Notice]()

user_service = UserService(user_repository)
sticker_service = StickerService(sticker_repository)
issue_service = IssueService(issue_repository, user_repository, sticker_repository)
notice_service = NoticeService(notice_repository, issue_repository)


def reset_storage() -> None:
    repositories = [user_repository, issue_repository, sticker_repository, notice_repository]
    for repository in repositories:
        repository._items.clear()
        repository._next_id = 1
