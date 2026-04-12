class Comment(Model):
    __table_name__ = "tbl_comment"
    __keyspace__ = "distcomp"

    issue_id = columns.BigInt(partition_key=True)
    id       = columns.BigInt(primary_key=True, clustering_order="ASC")
    content  = columns.Text()
    state    = columns.Text(default="PENDING")  # PENDING / APPROVE / DECLINE