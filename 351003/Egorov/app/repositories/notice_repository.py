from typing import List, Optional
from app.config.cassandra_config import cassandra_config
from app.models.notice import Notice


class NoticeRepository:
    def __init__(self):
        self.session = cassandra_config.session

    def create(self, notice: Notice) -> Notice:
        query = """
            INSERT INTO tbl_notice (country, story_id, id, content)
            VALUES (%s, %s, %s, %s)
        """
        self.session.execute(query, (notice.country, notice.story_id, notice.id, notice.content))
        return notice

    def get_by_id(self, notice_id: int) -> Optional[Notice]:
        # В Cassandra нужно знать partition key (country) для эффективного запроса
        # Это упрощенный вариант - в реальности нужно передавать country
        query = "SELECT * FROM tbl_notice WHERE id = %s ALLOW FILTERING"
        rows = self.session.execute(query, (notice_id,))
        for row in rows:
            return Notice.from_cassandra_row(row)
        return None

    def get_by_country_and_story(self, country: str, story_id: int) -> List[Notice]:
        query = "SELECT * FROM tbl_notice WHERE country = %s AND story_id = %s"
        rows = self.session.execute(query, (country, story_id))
        return [Notice.from_cassandra_row(row) for row in rows]

    def get_all(self) -> List[Notice]:
        query = "SELECT * FROM tbl_notice"
        rows = self.session.execute(query)
        return [Notice.from_cassandra_row(row) for row in rows]

    def update(self, notice: Notice) -> Notice:
        # В Cassandra UPDATE = INSERT (upsert)
        query = """
            INSERT INTO tbl_notice (country, story_id, id, content)
            VALUES (%s, %s, %s, %s)
        """
        self.session.execute(query, (notice.country, notice.story_id, notice.id, notice.content))
        return notice

    def delete(self, country: str, story_id: int, notice_id: int) -> bool:
        query = "DELETE FROM tbl_notice WHERE country = %s AND story_id = %s AND id = %s"
        self.session.execute(query, (country, story_id, notice_id))
        return True