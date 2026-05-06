from repository.in_memory import InMemoryRepository
from services.user import UserService
from services.topic import TopicService
from services.marker import MarkerService
from services.notice import NoticeService
from models.user import User
from models.topic import Topic
from models.marker import Marker
from models.notice import Notice

# Репозитории (синглтоны)
user_repo = InMemoryRepository[User]()
topic_repo = InMemoryRepository[Topic]()
marker_repo = InMemoryRepository[Marker]()
notice_repo = InMemoryRepository[Notice]()

# Предзаполнение первого пользователя согласно схеме
initial_user = User(
    login="elenthrill@gmail.com",
    password="password123",  # в реальном приложении – хеш
    firstname="Илья",
    lastname="Лавренов",
)
user_repo.create(initial_user)


def get_user_service() -> UserService:
    return UserService(user_repo)


def get_topic_service() -> TopicService:
    return TopicService(topic_repo)


def get_marker_service() -> MarkerService:
    return MarkerService(marker_repo)


def get_notice_service() -> NoticeService:
    return NoticeService(notice_repo)
