using Cassandra;
using Cassandra.Mapping;
using Discussion.Model;
using Microsoft.Extensions.Options;
using ISession = Cassandra.ISession;

namespace Discussion.Repository {
    public class CassandraReactionRepository : IReactionRepository {
        private readonly ICluster _cluster;
        private readonly ISession _session;
        private readonly IMapper _mapper;
        private readonly ILogger<CassandraReactionRepository> _logger;
        private readonly string _keyspace = "distcomp";

        public CassandraReactionRepository(
            IOptions<CassandraConfig> config,
            ILogger<CassandraReactionRepository> logger) {
            _logger = logger;

            var queryOptions = new QueryOptions().SetConsistencyLevel(ConsistencyLevel.LocalQuorum);

            _cluster = Cluster.Builder()
                .AddContactPoint(config.Value.Host)
                .WithPort(config.Value.Port)
                .WithQueryOptions(queryOptions)
                .Build();

            _session = _cluster.Connect(_keyspace);
            _mapper = new Mapper(_session);

            CreateTableIfNotExists();
        }

        private void CreateTableIfNotExists() {
            var createTableCql = @"
                CREATE TABLE IF NOT EXISTS tbl_reaction (
                    country text,
                    tweetId bigint,
                    id bigint,
                    content text,
                    created timestamp,
                    PRIMARY KEY ((country), tweetId, id)
                ) WITH CLUSTERING ORDER BY (tweetId ASC, id ASC)";

            _session.Execute(createTableCql);
            _logger.LogInformation("Ensured tbl_reaction exists in Cassandra");
        }

        public async Task<Reaction?> GetAsync(string country, long tweetId, long id) {
            var cql = "SELECT * FROM tbl_reaction WHERE country = ? AND tweetid = ? AND id = ?";
            var statement = await _session.PrepareAsync(cql);
            var bound = statement.Bind(country, tweetId, id);

            var result = await _session.ExecuteAsync(bound);
            var row = result.FirstOrDefault();

            return row == null ? null : MapRowToReaction(row);
        }

        public async Task<IEnumerable<Reaction>> GetByTweetIdAsync(string country, long tweetId) {
            var cql = "SELECT * FROM tbl_reaction WHERE country = ? AND tweetid = ?";
            var statement = await _session.PrepareAsync(cql);
            var bound = statement.Bind(country, tweetId);

            var result = await _session.ExecuteAsync(bound);
            return result.Select(MapRowToReaction).ToList();
        }

        public async Task<IEnumerable<Reaction>> GetByCountryAsync(string country) {
            var cql = "SELECT * FROM tbl_reaction WHERE country = ?";
            var statement = await _session.PrepareAsync(cql);
            var bound = statement.Bind(country);

            var result = await _session.ExecuteAsync(bound);
            return result.Select(MapRowToReaction).ToList();
        }

        public async Task<Reaction> AddAsync(Reaction reaction) {
            reaction.Created = DateTime.UtcNow;

            var cql = @"
                INSERT INTO tbl_reaction (country, tweetid, id, content, created) 
                VALUES (?, ?, ?, ?, ?)";

            var statement = await _session.PrepareAsync(cql);
            var bound = statement.Bind(
                reaction.Country,
                reaction.TweetId,
                reaction.Id,
                reaction.Content,
                reaction.Created
            );

            await _session.ExecuteAsync(bound);
            _logger.LogInformation($"Added reaction with ID {reaction.Id} for tweet {reaction.TweetId} in country {reaction.Country}");

            return reaction;
        }

        public async Task<Reaction> UpdateAsync(Reaction reaction) {
            var cql = @"
                UPDATE tbl_reaction 
                SET content = ? 
                WHERE country = ? AND tweetid = ? AND id = ?";

            var statement = await _session.PrepareAsync(cql);
            var bound = statement.Bind(
                reaction.Content,
                reaction.Country,
                reaction.TweetId,
                reaction.Id
            );

            await _session.ExecuteAsync(bound);
            _logger.LogInformation($"Updated reaction with ID {reaction.Id}");

            return reaction;
        }

        public async Task<bool> DeleteAsync(string country, long tweetId, long id) {
            var cql = "DELETE FROM tbl_reaction WHERE country = ? AND tweetid = ? AND id = ?";
            var statement = await _session.PrepareAsync(cql);
            var bound = statement.Bind(country, tweetId, id);

            await _session.ExecuteAsync(bound);
            _logger.LogInformation($"Deleted reaction with ID {id}");

            return true;
        }

        public async Task<bool> ExistsAsync(string country, long tweetId, long id) {
            var reaction = await GetAsync(country, tweetId, id);
            return reaction != null;
        }

        private Reaction MapRowToReaction(Row row) {
            return new Reaction {
                Country = row.GetValue<string>("country"),
                TweetId = row.GetValue<long>("tweetid"),
                Id = row.GetValue<long>("id"),
                Content = row.GetValue<string>("content"),
                Created = row.GetValue<DateTime>("created")
            };
        }

        public async Task<IEnumerable<Reaction>> FindByIdAsync(long id) {
            try {
                _logger.LogInformation($"Finding reactions by id {id} with ALLOW FILTERING");

                var cql = "SELECT * FROM tbl_reaction WHERE id = ? ALLOW FILTERING";
                var statement = await _session.PrepareAsync(cql);
                var bound = statement.Bind(id);
                var result = await _session.ExecuteAsync(bound);

                var reactions = result.Select(MapRowToReaction).ToList();

                _logger.LogInformation($"Found {reactions.Count} reactions with id {id}");
                return reactions;
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error finding reactions by id {id}");
                throw;
            }
        }

        public void Dispose() {
            _session?.Dispose();
            _cluster?.Dispose();
        }
    }
}