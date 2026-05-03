using Cassandra;

namespace Infrastructure.RepositoryImplementation
{
    public static class CassandraInitializer
    {
        public static async Task InitializeAsync(ISession session, string keyspaceName)
        {
            var createKeyspace = $@"
                CREATE KEYSPACE IF NOT EXISTS {keyspaceName}
                WITH REPLICATION = {{ 'class' : 'SimpleStrategy', 'replication_factor' : 1 }}";
                    await session.ExecuteAsync(new SimpleStatement(createKeyspace));
                    session.ChangeKeyspace(keyspaceName);

            await session.ExecuteAsync(new SimpleStatement(@"
                CREATE TABLE IF NOT EXISTS posts_by_id (
                    post_id INT PRIMARY KEY,
                    story_id INT,
                    content TEXT
                )"));

            await session.ExecuteAsync(new SimpleStatement(@"
                CREATE TABLE IF NOT EXISTS posts_by_story (
                    story_id INT,
                    bucket INT,
                    post_id INT,
                    content TEXT,
                    PRIMARY KEY ((story_id, bucket), post_id)
                ) WITH CLUSTERING ORDER BY (post_id DESC)"));
        }
    }
}
