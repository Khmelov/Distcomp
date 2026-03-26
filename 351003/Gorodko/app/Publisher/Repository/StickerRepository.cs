using Dapper;
using Npgsql;
using Publisher.Model;

namespace Publisher.Repository {
    public interface IStickerRepository : IRepository<Sticker> {
        Task<Sticker?> GetByNameAsync(string name);
        Task<IEnumerable<Sticker>> GetStickersByTweetIdAsync(long tweetId);
    }
    public class StickerRepository : PostgresRepository<Sticker>, IStickerRepository {
        public StickerRepository(IConfiguration configuration, ILogger<PostgresRepository<Sticker>> logger)
            : base(configuration, logger) {
        }

        public async Task<Sticker?> GetByNameAsync(string name) {
            const string sql = "SELECT * FROM tbl_sticker WHERE name = @name";

            using var connection = new NpgsqlConnection(_connectionString);
            return await connection.QueryFirstOrDefaultAsync<Sticker>(sql, new { name });
        }

        public async Task<IEnumerable<Sticker>> GetStickersByTweetIdAsync(long tweetId) {
            const string sql = @"
            SELECT s.* FROM tbl_sticker s
            INNER JOIN tbl_tweet_sticker ts ON ts.sticker_id = s.id
            WHERE ts.tweet_id = @TweetId";

            using var connection = new NpgsqlConnection(_connectionString);
            return await connection.QueryAsync<Sticker>(sql, new { TweetId = tweetId });
        }
    }
}