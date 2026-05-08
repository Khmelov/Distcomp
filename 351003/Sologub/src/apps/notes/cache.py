"""Redis-backed cache for notes in the REST (publisher) service.

Using Redis instead of in-process memory lets us run several publisher
instances behind a load balancer while sharing note state through a common
cache. Keys layout::

    tbl_note:{id}                -> JSON-encoded note object (string)
    tbl_note:index               -> SET of all note ids
    tbl_note:story:{storyId}     -> SET of note ids belonging to a story

The ``tbl_`` prefix is kept consistent with the DB-table naming convention
used elsewhere in the project.
"""

from __future__ import annotations

import json
import logging
import os
from typing import Optional, Iterable, List

import redis

logger = logging.getLogger(__name__)

_REDIS_HOST = os.environ.get("REDIS_HOST", "localhost")
_REDIS_PORT = int(os.environ.get("REDIS_PORT", "6379"))
_REDIS_DB = int(os.environ.get("REDIS_DB", "0"))

_NOTE_KEY = "tbl_note:{id}"
_NOTE_INDEX = "tbl_note:index"
_NOTE_STORY_INDEX = "tbl_note:story:{story_id}"

_client: Optional[redis.Redis] = None


def get_client() -> redis.Redis:
    global _client
    if _client is None:
        _client = redis.Redis(
            host=_REDIS_HOST,
            port=_REDIS_PORT,
            db=_REDIS_DB,
            decode_responses=True,
            socket_connect_timeout=2,
            socket_timeout=2,
        )
    return _client


def _key(note_id: int) -> str:
    return _NOTE_KEY.format(id=note_id)


def _story_key(story_id: int) -> str:
    return _NOTE_STORY_INDEX.format(story_id=story_id)


def save(note: dict) -> None:
    """Insert or update ``note`` atomically in the cache."""
    client = get_client()
    note_id = int(note["id"])
    story_id = int(note["storyId"])
    payload = json.dumps(note)

    previous = client.get(_key(note_id))
    pipe = client.pipeline()
    if previous:
        try:
            prev_story = int(json.loads(previous)["storyId"])
            if prev_story != story_id:
                pipe.srem(_story_key(prev_story), note_id)
        except (ValueError, KeyError, json.JSONDecodeError):
            pass

    pipe.set(_key(note_id), payload)
    pipe.sadd(_NOTE_INDEX, note_id)
    pipe.sadd(_story_key(story_id), note_id)
    pipe.execute()


def get(note_id: int) -> Optional[dict]:
    raw = get_client().get(_key(note_id))
    if not raw:
        return None
    try:
        return json.loads(raw)
    except json.JSONDecodeError:
        return None


def delete(note_id: int) -> Optional[dict]:
    client = get_client()
    raw = client.get(_key(note_id))
    if not raw:
        return None
    try:
        note = json.loads(raw)
    except json.JSONDecodeError:
        note = None

    pipe = client.pipeline()
    pipe.delete(_key(note_id))
    pipe.srem(_NOTE_INDEX, note_id)
    if note:
        try:
            pipe.srem(_story_key(int(note["storyId"])), note_id)
        except (KeyError, ValueError):
            pass
    pipe.execute()
    return note


def list_all(story_id: Optional[int] = None) -> List[dict]:
    client = get_client()
    if story_id is None:
        ids: Iterable = client.smembers(_NOTE_INDEX)
    else:
        ids = client.smembers(_story_key(story_id))
    if not ids:
        return []
    keys = [_key(int(i)) for i in ids]
    raw_values = client.mget(keys)
    notes: List[dict] = []
    for raw in raw_values:
        if not raw:
            continue
        try:
            notes.append(json.loads(raw))
        except json.JSONDecodeError:
            continue
    notes.sort(key=lambda n: int(n.get("id", 0)))
    return notes
