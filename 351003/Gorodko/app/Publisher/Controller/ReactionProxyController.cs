using Microsoft.AspNetCore.DataProtection.KeyManagement;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Dto;
using Publisher.DTO;
using Publisher.Model;
using Publisher.Service;
using System.Diagnostics.Metrics;
using System.Text;
using System.Text.Json;
using static Dapper.SqlMapper;

namespace Publisher.Controller {
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionProxyController : ControllerBase {
        private readonly KafkaService _kafkaService;
        private readonly ILogger<ReactionProxyController> _logger;
        private readonly JsonSerializerOptions _jsonOptions;
        private readonly IDistributedCache _cache;
        protected string _cacheKeyPrefix = "reaction:";

        public ReactionProxyController(KafkaService kafkaService, ILogger<ReactionProxyController> logger,
            IDistributedCache cache) {
            _kafkaService = kafkaService;
            _logger = logger;
            _jsonOptions = new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase };
            _cache = cache;
        }

        [HttpPost]
        public async Task<IActionResult> CreateReaction([FromBody] JsonElement requestBody) {
            _logger.LogInformation("Processing POST /reactions via Kafka");

            try {
                var reactionRequest = JsonSerializer.Deserialize<ReactionRequestTo>(requestBody.GetRawText(), new JsonSerializerOptions {
                    PropertyNamingPolicy = JsonNamingPolicy.CamelCase
                }) ?? new ReactionRequestTo();

                if (string.IsNullOrEmpty(reactionRequest.Country)) reactionRequest.Country = "by";

                long generatedId = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
                reactionRequest.Id = generatedId;
                reactionRequest.State = ReactionState.PENDING;

                string tweetIdStr = reactionRequest.TweetId.ToString();
                string correlationId = Guid.NewGuid().ToString();

                await _kafkaService.SendMessageAsync(tweetIdStr, reactionRequest, "POST", correlationId);

                return CreatedAtAction(nameof(GetReaction),
                    new { country = reactionRequest.Country, tweetId = reactionRequest.TweetId, id = generatedId },
                    reactionRequest);
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error processing POST reaction");
                return StatusCode(500, new { error = "Internal transport error" });
            }
        }

        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetReactionById(long id) {
            _logger.LogInformation($"Requesting GET /reactions/{id} via Kafka");
            try {
                string key = $"{_cacheKeyPrefix}{id}";

                var cachedData = await _cache.GetStringAsync(key);
                if (!string.IsNullOrEmpty(cachedData)) {
                    _logger.LogInformation($"Cache hit for reaction:{id}");
                    return Ok(JsonSerializer.Deserialize<ReactionResponseTo>(cachedData));
                }

                var requestData = new ReactionRequestTo { Id = id };
                var resultJson = await _kafkaService.SendAndAwaitAsync("0", requestData, "GET_BY_ID_ONLY");

                if (string.IsNullOrEmpty(resultJson) || resultJson == "{}") {
                    return NotFound();
                }

                var options = new DistributedCacheEntryOptions().SetAbsoluteExpiration(TimeSpan.FromMinutes(5));

                await _cache.SetStringAsync(key, resultJson, options);

                return Content(resultJson, "application/json");
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout" });
            }
        }

        [HttpGet]
        public async Task<IActionResult> GetReactions([FromQuery] string? country = null) {
            _logger.LogInformation("Requesting GET /reactions via Kafka");
            try {
                var requestData = new ReactionRequestTo { Country = country ?? "by" };

                var resultJson = await _kafkaService.SendAndAwaitAsync("0", requestData, "GET_ALL");

                return Ok(JsonDocument.Parse(resultJson).RootElement);
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout from Discussion service" });
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error in GET reactions via Kafka");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }

        [HttpGet("{country}/{tweetId}/{id}")]
        public async Task<IActionResult> GetReaction(string country, long tweetId, long id) {
            _logger.LogInformation($"Requesting GET /reactions/{country}/{tweetId}/{id} via Kafka");
            try {
                var requestData = new ReactionRequestTo { Country = country, TweetId = tweetId, Id = id };

                var resultJson = await _kafkaService.SendAndAwaitAsync(tweetId.ToString(), requestData, "GET_BY_ID");

                return Ok(JsonDocument.Parse(resultJson).RootElement);
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout" });
            }
        }

        [HttpGet("by-tweet/{tweetId}")]
        public async Task<IActionResult> GetReactionsByTweet(long tweetId, [FromQuery] string? country = null) {
            _logger.LogInformation($"Requesting GET /reactions/by-tweet/{tweetId} via Kafka");
            try {
                var requestData = new ReactionRequestTo { TweetId = tweetId, Country = country ?? "by" };
                var resultJson = await _kafkaService.SendAndAwaitAsync(tweetId.ToString(), requestData, "GET_BY_TWEET");

                return Ok(JsonDocument.Parse(resultJson).RootElement);
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout" });
            }
        }

        [HttpPut("{country}/{tweetId}/{id}")]
        public async Task<IActionResult> UpdateReaction(string country, long tweetId, long id, [FromBody] JsonElement request) {
            _logger.LogInformation($"Requesting PUT /reactions/{country}/{tweetId}/{id} via Kafka");
            try {
                var requestData = new ReactionRequestTo {
                    Country = country,
                    TweetId = tweetId,
                    Id = id,
                    Content = request.GetRawText()
                };

                var resultJson = await _kafkaService.SendAndAwaitAsync(tweetId.ToString(), requestData, "PUT");
                await _cache.RemoveAsync($"{_cacheKeyPrefix}{id}");

                return Ok(JsonDocument.Parse(resultJson).RootElement);
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout" });
            }
        }

        [HttpDelete("{country}/{tweetId}/{id}")]
        public async Task<IActionResult> DeleteReaction(string country, long tweetId, long id) {
            _logger.LogInformation($"Requesting DELETE /reactions/{country}/{tweetId}/{id} via Kafka");
            try {
                var requestData = new ReactionRequestTo { Country = country, TweetId = tweetId, Id = id };

                var resultJson = await _kafkaService.SendAndAwaitAsync(tweetId.ToString(), requestData, "DELETE");
                await _cache.RemoveAsync($"{_cacheKeyPrefix}{id}");
                return NoContent();
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout" });
            }
        }


        [HttpPut("{id:long}")]
        public async Task<IActionResult> UpdateReaction(long id, [FromBody] JsonElement request) {
            _logger.LogInformation($"PUT /reactions/{id} via Kafka");

            try {
                var requestData = new ReactionRequestTo() {
                    Id = id,
                    Content = request.GetRawText() 
                };

                var resultJson = await _kafkaService.SendAndAwaitAsync("0", requestData, "PUT_BY_ID");

                var jsonDoc = JsonDocument.Parse(resultJson);
                await _cache.RemoveAsync($"{_cacheKeyPrefix}{id}");
                return Ok(jsonDoc.RootElement);
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout: Discussion service did not respond" });
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error updating reaction {id} via Kafka");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }



        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeleteReactionById(long id) {
            _logger.LogInformation($"DELETE /reactions/{id} via Kafka");

            try {
                var requestData = new ReactionRequestTo() { Id = id };

                await _kafkaService.SendAndAwaitAsync("0", requestData, "DELETE_BY_ID");
                await _cache.RemoveAsync($"{_cacheKeyPrefix}{id}");
                return NoContent();
            }
            catch (TimeoutException) {
                return StatusCode(504, new { error = "Gateway Timeout" });
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error deleting reaction {id} via Kafka");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }
    }
}