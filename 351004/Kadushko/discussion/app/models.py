from cassandra.cqlengine.models import Model
from cassandra.cqlengine import columns

# Partition key: issue_id (равномерное распределение по нодам)
# Clustering key: id (уникальность внутри партиции)
# country как partition key — ошибка (мало уникальных значений = data skew)

class Comment(Model):
    __table_name__ = "tbl_comment"
    __keyspace__ = "distcomp"

    issue_id = columns.BigInt(partition_key=True)
    id       = columns.BigInt(primary_key=True, clustering_order="ASC")
    content  = columns.Text()
