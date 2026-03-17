from django.conf import settings
from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.request import Request


_NOTE_REPOSITORY = None


def _get_repository():
    global _NOTE_REPOSITORY
    if _NOTE_REPOSITORY is None:
        from repository import CassandraNoteRepository

        _NOTE_REPOSITORY = CassandraNoteRepository(
            host=getattr(settings, "CASSANDRA_HOST", "localhost"),
            port=getattr(settings, "CASSANDRA_PORT", 9042),
        )
    return _NOTE_REPOSITORY


class NoteViewSet(viewsets.ViewSet):

    def list(self, request: Request):
        story_id = request.query_params.get("storyId")
        if not story_id:
            return Response([], status=status.HTTP_200_OK)
        try:
            story_id = int(story_id)
        except (TypeError, ValueError):
            return Response(
                {"detail": "storyId must be an integer."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        try:
            repo = _get_repository()
            notes = repo.list_by_story(story_id)
            return Response([_note_to_json(n) for n in notes])
        except Exception as e:
            return Response(
                {"detail": str(e)},
                status=status.HTTP_503_SERVICE_UNAVAILABLE,
            )

    def create(self, request: Request):
        data = request.data if isinstance(request.data, dict) else {}
        story_id = data.get("storyId")
        content = data.get("content") or ""
        country = data.get("country", "")
        if story_id is None:
            return Response(
                {"detail": "storyId is required."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        if len(content) < 2 or len(content) > 2048:
            return Response(
                {"detail": "content must be between 2 and 2048 characters."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        try:
            story_id = int(story_id)
        except (TypeError, ValueError):
            return Response(
                {"detail": "storyId must be an integer."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        try:
            repo = _get_repository()
            note = repo.create(story_id=story_id, content=content, country=country or "")
            return Response(_note_to_json(note), status=status.HTTP_201_CREATED)
        except Exception as e:
            return Response(
                {"detail": str(e)},
                status=status.HTTP_503_SERVICE_UNAVAILABLE,
            )

    def retrieve(self, request: Request, pk=None):
        try:
            note_id = int(pk)
        except (TypeError, ValueError):
            return Response(
                {"detail": "Invalid note id."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        try:
            repo = _get_repository()
            note = repo.get_by_id(note_id)
        except Exception as e:
            return Response(
                {"detail": str(e)},
                status=status.HTTP_503_SERVICE_UNAVAILABLE,
            )
        if not note:
            return Response(status=status.HTTP_404_NOT_FOUND)
        return Response(_note_to_json(note))

    def update(self, request: Request, pk=None):
        try:
            note_id = int(pk)
        except (TypeError, ValueError):
            return Response(
                {"detail": "Invalid note id."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        data = request.data if isinstance(request.data, dict) else {}
        story_id = data.get("storyId")
        content = data.get("content") or ""
        country = data.get("country", "")
        if story_id is None:
            return Response(
                {"detail": "storyId is required."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        if len(content) < 2 or len(content) > 2048:
            return Response(
                {"detail": "content must be between 2 and 2048 characters."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        try:
            story_id = int(story_id)
        except (TypeError, ValueError):
            return Response(
                {"detail": "storyId must be an integer."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        try:
            repo = _get_repository()
            note = repo.update(story_id=story_id, note_id=note_id, content=content, country=country or "")
        except Exception as e:
            return Response(
                {"detail": str(e)},
                status=status.HTTP_503_SERVICE_UNAVAILABLE,
            )
        if not note:
            return Response(status=status.HTTP_404_NOT_FOUND)
        return Response(_note_to_json(note))

    def destroy(self, request: Request, pk=None):
        try:
            note_id = int(pk)
        except (TypeError, ValueError):
            return Response(
                {"detail": "Invalid note id."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        data = getattr(request, "data", None) or {}
        if isinstance(data, dict):
            story_id = request.query_params.get("storyId") or data.get("storyId")
        else:
            story_id = request.query_params.get("storyId")
        if story_id is None:
            try:
                repo = _get_repository()
                note = repo.get_by_id(note_id)
            except Exception as e:
                return Response(
                    {"detail": str(e)},
                    status=status.HTTP_503_SERVICE_UNAVAILABLE,
                )
            if not note:
                return Response(status=status.HTTP_404_NOT_FOUND)
            story_id = note.storyId
        else:
            try:
                story_id = int(story_id)
            except (TypeError, ValueError):
                return Response(
                    {"detail": "storyId must be an integer."},
                    status=status.HTTP_400_BAD_REQUEST,
                )
        try:
            repo = _get_repository()
            if not repo.delete(story_id=story_id, note_id=note_id):
                return Response(status=status.HTTP_404_NOT_FOUND)
        except Exception as e:
            return Response(
                {"detail": str(e)},
                status=status.HTTP_503_SERVICE_UNAVAILABLE,
            )
        return Response(status=status.HTTP_204_NO_CONTENT)


def _note_to_json(note) -> dict:
    return {
        "id": note.id,
        "storyId": note.storyId,
        "content": note.content,
        "country": note.country or "",
    }
