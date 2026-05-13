"""Генерация уникальных целочисленных id без горячей партиции счётчика в Cassandra."""

from __future__ import annotations

import threading
import time

_lock = threading.Lock()
_seq = 0


def next_note_id() -> int:
    global _seq
    with _lock:
        _seq = (_seq + 1) & 0xFFF
        ms = int(time.time() * 1000) & ((1 << 40) - 1)
        return (ms << 12) | _seq
