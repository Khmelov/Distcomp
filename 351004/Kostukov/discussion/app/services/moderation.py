from discussion.app.schemas import NoteState

class NoteModerationService:
    def __init__(self) -> None:
        self.stop_words = {
            "spam",
            "scam",
            "fraud",
            "badword",
        }

    def moderate(self, content: str) -> NoteState:
        text = (content or "").lower()
        for word in self.stop_words:
            if word in text:
                return NoteState.DECLINE
        return NoteState.APPROVE