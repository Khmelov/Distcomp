from clients.discussion_client import DiscussionClient


class NoteStateService:
    def __init__(self):
        self.discussion_client = DiscussionClient()

    async def update_note_state(self, issue_id: int, note_id: int, state: str):
        """Обновление состояния заметки через REST в discussion"""
        # Получаем текущую заметку
        note = await self.discussion_client.get_note(issue_id, note_id)
        if note:
            # Обновляем state через отдельный эндпоинт
            await self.discussion_client.update_note_state(issue_id, note_id, state)