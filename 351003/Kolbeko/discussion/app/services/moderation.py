import re


_STOP_WORDS = {"spam", "badword", "curse"}


def moderate(text: str) -> str:
    words = {w.lower() for w in re.findall(r"\w+", text)}
    if words & _STOP_WORDS:
        return "DECLINE"
    return "APPROVE"

