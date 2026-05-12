
def validate(s: str):
    stop_words = {"spam", "ban", "hate"}
    for w in stop_words:
        if w in s:
            return False
    return True