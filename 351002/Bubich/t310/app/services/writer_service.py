from typing import List
from app.models.writer import Writer
from app.dto.requests.writer_request import WriterRequestTo
from app.dto.responses.writer_response import WriterResponseTo
from app.repository.in_memory_repository import InMemoryRepository
from app.exceptions.custom_exceptions import NotFoundError, ValidationError


class WriterService:
    def __init__(self, repository: InMemoryRepository):
        self.repository = repository

    def create(self, request: WriterRequestTo) -> WriterResponseTo:
        self._validate_request(request)
        writer = Writer(
            login=request.login,
            password=request.password,
            firstname=request.firstname,
            lastname=request.lastname
        )
        saved_writer = self.repository.save(writer)
        return self._to_response(saved_writer)

    def get_by_id(self, id: int) -> WriterResponseTo:
        writer = self.repository.find_by_id(id)
        if not writer:
            raise NotFoundError(f"Writer with id {id} not found")
        return self._to_response(writer)

    def get_all(self) -> List[WriterResponseTo]:
        writers = self.repository.find_all()
        return [self._to_response(w) for w in writers]

    def update(self, id: int, request: WriterRequestTo) -> WriterResponseTo:
        self._validate_request(request)
        existing_writer = self.repository.find_by_id(id)
        if not existing_writer:
            raise NotFoundError(f"Writer with id {id} not found")

        existing_writer.login = request.login
        existing_writer.password = request.password
        existing_writer.firstname = request.firstname
        existing_writer.lastname = request.lastname

        updated_writer = self.repository.update(existing_writer)
        return self._to_response(updated_writer)

    def delete(self, id: int) -> None:
        if not self.repository.delete_by_id(id):
            raise NotFoundError(f"Writer with id {id} not found")

    def get_by_story_id(self, story_repo: InMemoryRepository, story_id: int) -> WriterResponseTo:
        story = story_repo.find_by_id(story_id)
        if not story:
            raise NotFoundError(f"Story with id {story_id} not found")

        writer = self.repository.find_by_id(story.writer_id)
        if not writer:
            raise NotFoundError(f"Writer for story {story_id} not found")

        return self._to_response(writer)

    def _validate_request(self, request: WriterRequestTo):
        if not request.login or len(request.login) < 2 or len(request.login) > 64:
            raise ValidationError("Login must be between 2 and 64 characters")
        if not request.password or len(request.password) < 8 or len(request.password) > 128:
            raise ValidationError("Password must be between 8 and 128 characters")
        if not request.firstname or len(request.firstname) < 2 or len(request.firstname) > 64:
            raise ValidationError("Firstname must be between 2 and 64 characters")
        if not request.lastname or len(request.lastname) < 2 or len(request.lastname) > 64:
            raise ValidationError("Lastname must be between 2 and 64 characters")

    def _to_response(self, writer: Writer) -> WriterResponseTo:
        return WriterResponseTo(
            id=writer.id,
            login=writer.login,
            firstname=writer.firstname,
            lastname=writer.lastname
        )