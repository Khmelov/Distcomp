import time
from datetime import datetime
from cassandra.cqlengine import columns, models


class CommentModel(models.Model):
    __table_name__ = 'tbl_comment'

    # partition key per schema: country
    country = columns.Text(partition_key=True)
    # clustering keys: issueId, id
    issueId = columns.BigInt(primary_key=True)
    id = columns.BigInt(primary_key=True, default=lambda: int(time.time() * 1000))
    content = columns.Text(required=True, min_length=2, max_length=2048)


class CommentByIdModel(models.Model):
    __table_name__ = 'tbl_comment_by_id'

    id = columns.BigInt(primary_key=True)
    country = columns.Text()
    issueId = columns.BigInt()
    content = columns.Text(required=True, min_length=2, max_length=2048)
    created = columns.DateTime(default=datetime.utcnow)
    modified = columns.DateTime(default=datetime.utcnow)
