import time
from datetime import datetime
from cassandra.cqlengine import columns, models


class CommentModel(models.Model):
    __table_name__ = 'tbl_comment'

    # partition key: issue_id (быстрая выборка по Issue)
    issue_id = columns.BigInt(partition_key=True)

    # clustering keys: country, id (порядок важен для сортировки внутри партиции)
    country = columns.Text(primary_key=True)
    id = columns.BigInt(primary_key=True, default=lambda: int(time.time() * 1000))

    content = columns.Text(required=True, min_length=2, max_length=2048)
    created = columns.DateTime(default=lambda: datetime.utcnow())
    modified = columns.DateTime(default=lambda: datetime.utcnow())


# Вторая таблица для быстрого получения по id (для API GET /comments/{id})
class CommentByIdModel(models.Model):
    __table_name__ = 'tbl_comment_by_id'

    id = columns.BigInt(primary_key=True)
    issue_id = columns.BigInt(index=True)
    country = columns.Text()
    content = columns.Text(required=True, min_length=2, max_length=2048)
    created = columns.DateTime()
    modified = columns.DateTime()
