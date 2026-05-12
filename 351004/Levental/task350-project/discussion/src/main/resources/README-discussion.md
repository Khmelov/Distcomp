Discussion module stores Notice in Cassandra.
Partition key: article_id.
Clustering key: id.
This is intentionally modeled around the common query "find notices by articleId" to avoid poor distribution from the original scheme.
