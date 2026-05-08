from clients.discussion_client import DiscussionClient

class NoteStateService:
    def __init__(self):
        self.discussion_client = DiscussionClient()

    async def update_note_state(self, issue_id: int, note_id: int, state: str):
        # Получаем текущую заметку (сначала проверит кеш Redis)
        note = await self.discussion_client.get_note(issue_id, note_id)
        if note:
            # Обновляем state (и инвалидируем кеш внутри метода)
            await self.discussion_client.update_note_state(issue_id, note_id, state)