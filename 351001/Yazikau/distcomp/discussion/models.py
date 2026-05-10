from cassandra.cqlengine import columns
from cassandra.cqlengine.models import Model

class CassandraComment(Model):
    # Строго по схеме задания:
    country = columns.Text(partition_key=True)
    tweetId = columns.BigInt(primary_key=True)
    id = columns.BigInt(primary_key=True)
    content = columns.Text(min_length=2, max_length=2048)

    class Meta:
        db_table = 'tbl_comment'