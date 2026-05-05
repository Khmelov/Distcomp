using Cassandra;

namespace Infrastructure.RepositoryImplementation
{
    public static class CassandraInitializer
    {
        public static async Task InitializeAsync(ISession session, string keyspaceName)
        {
            var createKeyspace = $@"
                CREATE KEYSPACE IF NOT EXISTS {keyspaceName}
                WITH REPLICATION = {{ 
                    'class' : 'SimpleStrategy', 
                    'replication_factor' : 1 
                }}";

            await session.ExecuteAsync(new SimpleStatement(createKeyspace));

            session.ChangeKeyspace(keyspaceName);

            var createPostsTable = @"
                CREATE TABLE IF NOT EXISTS posts (
                    id INT PRIMARY KEY,
                    story_id INT,
                    content TEXT
                )";

            await session.ExecuteAsync(new SimpleStatement(createPostsTable));

            Console.WriteLine($"Cassandra initialized with keyspace: {keyspaceName}");
        }
    }
}
