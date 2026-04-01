using Dapper;
using Npgsql;
using Publisher.Model;
using Publisher.Repository.Params;

namespace Publisher.Repository {
    public class TweetRepository : PostgresRepository<Tweet>, IRepository<Tweet> {
        public TweetRepository(IConfiguration configuration, ILogger<PostgresRepository<Tweet>> logger)
            : base(configuration, logger) {
            _tableName = "tbl_tweet";
        }

        public async Task<Editor?> GetEditorByTweetIdAsync(long tweetId) {
            const string sql = @"
            SELECT e.* FROM tbl_editor e
            INNER JOIN tbl_tweet t ON t.editor_id = e.id
            WHERE t.id = @tweetId";

            using var connection = new NpgsqlConnection(_connectionString);
            return await connection.QueryFirstOrDefaultAsync<Editor>(sql, new { tweetId });
        }

        public async Task<IEnumerable<Sticker>> GetStickersByTweetIdAsync(long tweetId) {
            const string sql = @"
            SELECT s.* FROM tbl_sticker s
            INNER JOIN tbl_tweet_sticker ts ON ts.sticker_id = s.id
            WHERE ts.tweet_id = @tweetId";

            using var connection = new NpgsqlConnection(_connectionString);
            return await connection.QueryAsync<Sticker>(sql, new { tweetId });
        }

        public async Task<IEnumerable<Reaction>> GetReactionsByTweetIdAsync(long tweetId) {
            const string sql = @"
            SELECT * FROM tbl_reaction 
            WHERE tweet_id = @tweetId";

            using var connection = new NpgsqlConnection(_connectionString);
            return await connection.QueryAsync<Reaction>(sql, new { tweetId });
        }

        public async Task<IEnumerable<Tweet>> GetTweetsByParamsAsync(
            IEnumerable<string>? stickerNames = null,
            IEnumerable<long>? stickerIds = null,
            string? editorLogin = null,
            string? title = null,
            string? content = null) {
            var sql = @"
            SELECT DISTINCT t.* FROM tbl_tweet t
            LEFT JOIN tbl_editor e ON e.id = t.editor_id
            LEFT JOIN tbl_tweet_sticker ts ON ts.tweet_id = t.id
            LEFT JOIN tbl_sticker s ON s.id = ts.sticker_id
            WHERE 1=1";

            var parameters = new DynamicParameters();

            if (!string.IsNullOrEmpty(editorLogin)) {
                sql += " AND e.login ILIKE @EditorLogin";
                parameters.Add("EditorLogin", $"%{editorLogin}%");
            }

            if (!string.IsNullOrEmpty(title)) {
                sql += " AND t.title ILIKE @Title";
                parameters.Add("Title", $"%{title}%");
            }

            if (!string.IsNullOrEmpty(content)) {
                sql += " AND t.content ILIKE @Content";
                parameters.Add("Content", $"%{content}%");
            }

            if (stickerNames?.Any() == true) {
                sql += " AND s.name = ANY(@StickerNames)";
                parameters.Add("StickerNames", stickerNames.ToArray());
            }

            if (stickerIds?.Any() == true) {
                sql += " AND s.id = ANY(@StickerIds)";
                parameters.Add("StickerIds", stickerIds.ToArray());
            }

            using var connection = new NpgsqlConnection(_connectionString);
            return await connection.QueryAsync<Tweet>(sql, parameters);
        }

        protected override string BuildWhereClause(QueryParams queryParams) {
            if (queryParams is not SearchParams searchParams)
                return "";

            var conditions = new List<string>();

            if (searchParams.EditorId.HasValue)
                conditions.Add("editor_id = @EditorId");

            if (!string.IsNullOrEmpty(searchParams.Title))
                conditions.Add("title ILIKE '%' || @Title || '%'");

            if (!string.IsNullOrEmpty(searchParams.Content))
                conditions.Add("content ILIKE '%' || @Content || '%'");

            if (searchParams.FromDate.HasValue)
                conditions.Add("created >= @FromDate");

            if (searchParams.ToDate.HasValue)
                conditions.Add("created <= @ToDate");

            return conditions.Any() ? "WHERE " + string.Join(" AND ", conditions) : "";
        }
    }
}