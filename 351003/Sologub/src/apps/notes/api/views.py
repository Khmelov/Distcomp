import logging
import random
import time

from rest_framework import viewsets, status
from rest_framework.response import Response
from rest_framework.request import Request

from apps.notes import cache as notes_cache

logger = logging.getLogger(__name__)


# ---------------------------------------------------------------------------
# REST (publisher) service for Notes.
# Redis is used as a shared cache / store so the service stays stateless and
# can be scaled horizontally. Every mutation is also published to Kafka so
# that the discussion service reconciles the authoritative state in Cassandra.
# ---------------------------------------------------------------------------


def _generate_id() -> int:
    return (int(time.time() * 1_000_000) << 10) + random.randint(0, 1023)


def _publish(action: str, **payload) -> None:
    try:
        from apps.notes.kafka_producer import send_note_event
        send_note_event(action=action, **payload)
    except Exception as exc:
        logger.warning("Kafka publish failed (%s): %s", action, exc)


def _validate_payload(data: dict):
    story_id = data.get("storyId")
    content = data.get("content") or ""
    country = data.get("country", "") or ""

    if story_id is None:
        return None, Response(
            {"detail": "storyId is required."},
            status=status.HTTP_400_BAD_REQUEST,
        )
    try:
        story_id = int(story_id)
    except (TypeError, ValueError):
        return None, Response(
            {"detail": "storyId must be an integer."},
            status=status.HTTP_400_BAD_REQUEST,
        )
    if len(content) < 2 or len(content) > 2048:
        return None, Response(
            {"detail": "content must be between 2 and 2048 characters."},
            status=status.HTTP_400_BAD_REQUEST,
        )
    return {"storyId": story_id, "content": content, "country": country}, None


def _parse_id(pk):
    try:
        return int(pk), None
    except (TypeError, ValueError):
        return None, Response(
            {"detail": "Invalid note id."},
            status=status.HTTP_400_BAD_REQUEST,
        )


class NoteViewSet(viewsets.ViewSet):

    def list(self, request: Request):
        story_id = request.query_params.get("storyId")
        if story_id is not None:
            try:
                story_id = int(story_id)
            except (TypeError, ValueError):
                return Response(
                    {"detail": "storyId must be an integer."},
                    status=status.HTTP_400_BAD_REQUEST,
                )
        try:
            notes = notes_cache.list_all(story_id=story_id)
        except Exception as exc:
            logger.exception("Redis list failure")
            return Response({"detail": f"Cache unavailable: {exc}"},
                            status=status.HTTP_503_SERVICE_UNAVAILABLE)
        return Response(notes, status=status.HTTP_200_OK)

    def create(self, request: Request):
        data = request.data if isinstance(request.data, dict) else {}
        clean, err = _validate_payload(data)
        if err is not None:
            return err
        note_id = _generate_id()
        note = {
            "id": note_id,
            "storyId": clean["storyId"],
            "content": clean["content"],
            "country": clean["country"],
            "state": "PENDING",
        }
        try:
            notes_cache.save(note)
        except Exception as exc:
            logger.exception("Redis save failure")
            return Response({"detail": f"Cache unavailable: {exc}"},
                            status=status.HTTP_503_SERVICE_UNAVAILABLE)
        _publish("create", note_id=note_id, story_id=clean["storyId"],
                 content=clean["content"], country=clean["country"])
        return Response(note, status=status.HTTP_201_CREATED)

    def retrieve(self, request: Request, pk=None):
        note_id, err = _parse_id(pk)
        if err is not None:
            return err
        try:
            note = notes_cache.get(note_id)
        except Exception as exc:
            logger.exception("Redis get failure")
            return Response({"detail": f"Cache unavailable: {exc}"},
                            status=status.HTTP_503_SERVICE_UNAVAILABLE)
        if not note:
            return Response({"detail": "Note not found."},
                            status=status.HTTP_404_NOT_FOUND)
        return Response(note, status=status.HTTP_200_OK)

    def update(self, request: Request, pk=None):
        data = request.data if isinstance(request.data, dict) else {}
        if pk is None:
            pk = data.get("id")
        note_id, err = _parse_id(pk)
        if err is not None:
            return err
        clean, err = _validate_payload(data)
        if err is not None:
            return err
        try:
            existing = notes_cache.get(note_id)
            if not existing:
                return Response({"detail": "Note not found."},
                                status=status.HTTP_404_NOT_FOUND)
            note = {
                "id": note_id,
                "storyId": clean["storyId"],
                "content": clean["content"],
                "country": clean["country"],
                "state": existing.get("state", "PENDING"),
            }
            notes_cache.save(note)
        except Exception as exc:
            logger.exception("Redis update failure")
            return Response({"detail": f"Cache unavailable: {exc}"},
                            status=status.HTTP_503_SERVICE_UNAVAILABLE)
        _publish("update", note_id=note_id, story_id=clean["storyId"],
                 content=clean["content"], country=clean["country"])
        return Response(note, status=status.HTTP_200_OK)

    def partial_update(self, request: Request, pk=None):
        return self.update(request, pk=pk)

    def destroy(self, request: Request, pk=None):
        note_id, err = _parse_id(pk)
        if err is not None:
            return err
        try:
            removed = notes_cache.delete(note_id)
        except Exception as exc:
            logger.exception("Redis delete failure")
            return Response({"detail": f"Cache unavailable: {exc}"},
                            status=status.HTTP_503_SERVICE_UNAVAILABLE)
        if not removed:
            return Response({"detail": "Note not found."},
                            status=status.HTTP_404_NOT_FOUND)
        _publish("delete", note_id=note_id, story_id=int(removed["storyId"]))
        return Response(status=status.HTTP_204_NO_CONTENT)
