from typing import Dict, Any, Optional, List


class NoteModel:
    def __init__(self, session):
        self.session = session

    def get_next_id(self) -> int:
        """Получение следующего ID для заметки в рамках конкретного issue"""
        # В реальном проекте лучше использовать UUID, но для совместимости оставим int
        query = "SELECT MAX(id) as max_id FROM tbl_note"
        result = self.session.execute(query).one()
        next_id = (result['max_id'] + 1) if result and result['max_id'] else 1
        return next_id

    def create(self, data: Dict[str, Any]) -> Optional[Dict[str, Any]]:
        """Создание заметки"""
        query = """
                INSERT INTO tbl_note (issue_id, id, content)
                VALUES (%(issue_id)s, %(id)s, %(content)s) \
                """
        self.session.execute(query, data)
        return self.get_by_id(data['issue_id'], data['id'])

    def get_by_id(self, issue_id: int, note_id: int) -> Optional[Dict[str, Any]]:
        """Получение заметки по issue_id и id"""
        query = "SELECT issue_id, id, content FROM tbl_note WHERE issue_id = %s AND id = %s"
        result = self.session.execute(query, (issue_id, note_id)).one()
        return result

    def get_all_by_issue(self, issue_id: int) -> List[Dict[str, Any]]:
        """Все заметки одного issue"""
        query = "SELECT issue_id, id, content FROM tbl_note WHERE issue_id = %s"
        return list(self.session.execute(query, (issue_id,)))

    def get_all(self) -> List[Dict[str, Any]]:
        """Все заметки (неэффективно, но для совместимости)"""
        query = "SELECT issue_id, id, content FROM tbl_note"
        return list(self.session.execute(query))

    def update(self, issue_id: int, note_id: int, content: str) -> Optional[Dict[str, Any]]:
        """Обновление заметки"""
        query = """
                UPDATE tbl_note \
                SET content = %s
                WHERE issue_id = %s \
                  AND id = %s \
                """
        self.session.execute(query, (content, issue_id, note_id))
        return self.get_by_id(issue_id, note_id)

    def delete(self, issue_id: int, note_id: int) -> bool:
        """Удаление заметки"""
        query = "DELETE FROM tbl_note WHERE issue_id = %s AND id = %s"
        self.session.execute(query, (issue_id, note_id))
        return True