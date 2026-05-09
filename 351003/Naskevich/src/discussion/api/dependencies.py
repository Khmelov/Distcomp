from typing import Annotated

from cassandra.cluster import Session
from fastapi import Depends, Request

from src.config import PublisherConfig
from src.discussion.publisher_client import PublisherClient
from src.discussion.repositories.post import CassandraPostRepository
from src.discussion.services.post import PostService


def get_cassandra_session(request: Request) -> Session:
    return request.app.state.cassandra_session


CassandraSessionDep = Annotated[Session, Depends(get_cassandra_session)]


def get_post_repository(session: CassandraSessionDep) -> CassandraPostRepository:
    return CassandraPostRepository(session)


def get_publisher_client() -> PublisherClient:
    return PublisherClient(PublisherConfig().base_url)


def get_post_service(
    repo: Annotated[CassandraPostRepository, Depends(get_post_repository)],
    publisher: Annotated[PublisherClient, Depends(get_publisher_client)],
) -> PostService:
    return PostService(repository=repo, publisher=publisher)


PostServiceDep = Annotated[PostService, Depends(get_post_service)]
