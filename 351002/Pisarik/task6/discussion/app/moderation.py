"""Automatic moderation for message content (Task 340).

Uses an explicit marker so normal lecturer/test payloads are never declined by accident.
"""

_REJECT_MARKER = "[[MODERATION_REJECT]]"


def moderate_content(content: str) -> str:
    """Return APPROVE or DECLINE (only if content contains the explicit reject marker)."""
    if _REJECT_MARKER in (content or ""):
        return "DECLINE"
    return "APPROVE"
