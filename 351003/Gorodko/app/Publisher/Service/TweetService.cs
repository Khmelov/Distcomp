using AutoMapper;
using Dapper;
using Microsoft.Extensions.Caching.Distributed;
using Npgsql;
using Publisher.Dto;
using Publisher.Exceptions;
using Publisher.Model;
using Publisher.Repository;
using Publisher.Service;
using System.Net.Http;

namespace Publisher.Service {
    public class TweetService : BaseService<Tweet, TweetRequestTo, TweetResponseTo> {
        private readonly IRepository<Tweet> _tweetRepository;
        private readonly IRepository<Editor> _editorRepository;
        private readonly IStickerRepository _stickerRepository;
        private readonly HttpClient _discussionClient;

        public TweetService(
            IRepository<Tweet> repository,
            IMapper mapper,
            ILogger<TweetService> logger,
            IRepository<Editor> editorRepository,
            IStickerRepository stickerRepository,
            IHttpClientFactory httpClientFactory,
            IDistributedCache cache)
            : base(repository, mapper, logger, cache) {
            _tweetRepository = repository;
            _editorRepository = editorRepository;
            _stickerRepository = stickerRepository;
            _discussionClient = httpClientFactory.CreateClient("DiscussionClient");
            _discussionClient.BaseAddress = new Uri("http://localhost:24130");
            _cacheKeyPrefix = "tweet:";
        }

        public async Task<TweetResponseTo> CreateTweetAsync(TweetRequestTo request) {
            _logger.LogInformation($"Creating tweet with EditorId: {request.EditorId}, Stickers: {request.Stickers?.Count ?? 0}");

            var editor = await _editorRepository.GetByIdAsync(request.EditorId);
            if (editor == null) {
                throw new ValidationException($"Editor with id {request.EditorId} not found");
            }

            var existingTweet = await GetTweetByTitleAsync(request.Title);
            if (existingTweet != null) {
                throw new ForbiddenException($"Tweet with title '{request.Title}' already exists");
            }

            var tweet = new Tweet {
                EditorId = request.EditorId,
                Title = request.Title,
                Content = request.Content,
                Created = DateTime.UtcNow,
                Modified = DateTime.UtcNow
            };

            var createdTweet = await _tweetRepository.AddAsync(tweet);

            if (request.Stickers != null && request.Stickers.Any()) {
                await ProcessStickersAsync(createdTweet.Id, request.Stickers);
            }

            var tweetWithStickers = await GetTweetWithStickersAsync(createdTweet.Id);

            var response = _mapper.Map<TweetResponseTo>(tweetWithStickers);

            if (tweetWithStickers?.TweetStickers != null) {
                response.Stickers = tweetWithStickers.TweetStickers
                    .Select(ts => ts.Sticker?.Name)
                    .Where(name => name != null)
                    .ToList()!;
            }

            _logger.LogInformation($"Tweet created with ID: {response.Id}, Stickers: {response.Stickers?.Count ?? 0}");
            return response;
        }

        private async Task ProcessStickersAsync(long tweetId, List<string> stickerNames) {
            _logger.LogInformation($"Processing {stickerNames.Count} stickers for tweet {tweetId}");

            foreach (var stickerName in stickerNames.Distinct()) {
                var sticker = await _stickerRepository.GetByNameAsync(stickerName);

                if (sticker == null) {
                    _logger.LogInformation($"Creating new sticker: {stickerName}");

                    var newSticker = new Sticker { Name = stickerName };
                    sticker = await _stickerRepository.AddAsync(newSticker);
                }

                await LinkStickerToTweetAsync(tweetId, sticker.Id);
            }
        }

        private async Task LinkStickerToTweetAsync(long tweetId, long stickerId) {
            const string sql = @"
            INSERT INTO tbl_tweet_sticker (tweet_id, sticker_id) 
            VALUES (@TweetId, @StickerId)
            ON CONFLICT (tweet_id, sticker_id) DO NOTHING";

            using var connection = new NpgsqlConnection(_tweetRepository.GetConnectionString());
            await connection.ExecuteAsync(sql, new { TweetId = tweetId, StickerId = stickerId });

            _logger.LogDebug($"Linked sticker {stickerId} to tweet {tweetId}");
        }

        private async Task<Tweet?> GetTweetWithStickersAsync(long tweetId) {
            const string sql = @"
            SELECT 
                t.*,
                s.id as StickerId,
                s.name as StickerName
            FROM tbl_tweet t
            LEFT JOIN tbl_tweet_sticker ts ON ts.tweet_id = t.id
            LEFT JOIN tbl_sticker s ON s.id = ts.sticker_id
            WHERE t.id = @TweetId";

            using var connection = new NpgsqlConnection(_tweetRepository.GetConnectionString());

            var tweetDict = new Dictionary<long, Tweet>();

            var result = await connection.QueryAsync<Tweet, Sticker, Tweet>(
                sql,
                (tweet, sticker) => {
                    if (!tweetDict.TryGetValue(tweet.Id, out var tweetEntry)) {
                        tweetEntry = tweet;
                        tweetEntry.TweetStickers = new List<TweetSticker>();
                        tweetDict.Add(tweetEntry.Id, tweetEntry);
                    }

                    if (sticker != null) {
                        tweetEntry.TweetStickers.Add(new TweetSticker {
                            TweetId = tweetEntry.Id,
                            StickerId = sticker.Id,
                            Sticker = sticker
                        });
                    }

                    return tweetEntry;
                },
                new { TweetId = tweetId },
                splitOn: "StickerId");

            return tweetDict.Values.FirstOrDefault();
        }

        private async Task<Tweet?> GetTweetByTitleAsync(string title) {
            const string sql = "SELECT * FROM tbl_tweet WHERE title = @title";

            using var connection = new NpgsqlConnection(_tweetRepository.GetConnectionString());
            return await connection.QueryFirstOrDefaultAsync<Tweet>(sql, new { title });
        }

        public async Task<TweetResponseTo> GetTweetWithReactionsAsync(long id, string? country = null) {
            var tweet = await GetByIdAsync(id);
            if (tweet == null) return null;

            var reactionsResponse = await _discussionClient.GetAsync(
                $"/api/v1.0/reactions/by-tweet/{id}?country={country ?? "by"}");

            if (reactionsResponse.IsSuccessStatusCode) {
                var reactions = await reactionsResponse.Content
                    .ReadFromJsonAsync<IEnumerable<ReactionResponseTo>>();
            }

            return tweet;
        }
    }
}