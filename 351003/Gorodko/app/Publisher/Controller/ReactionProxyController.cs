using Microsoft.AspNetCore.Mvc;
using Publisher.Model;
using System.Diagnostics.Metrics;
using System.Text;
using System.Text.Json;

namespace Publisher.Controller {
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionProxyController : ControllerBase {
        private readonly HttpClient _discussionClient;
        private readonly ILogger<ReactionProxyController> _logger;
        private readonly JsonSerializerOptions _jsonOptions;

        public ReactionProxyController(IHttpClientFactory httpClientFactory, ILogger<ReactionProxyController> logger) {
            _discussionClient = httpClientFactory.CreateClient("DiscussionClient");
            _discussionClient.BaseAddress = new Uri("http://localhost:24130");
            _logger = logger;

            _jsonOptions = new JsonSerializerOptions {
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };
        }

        [HttpGet]
        public async Task<IActionResult> GetReactions([FromQuery] string? country = null) {
            _logger.LogInformation("Proxying GET /reactions to Discussion");

            try {
                var url = country == null
                    ? "/api/v1.0/reactions"
                    : $"/api/v1.0/reactions?country={country}";

                var response = await _discussionClient.GetAsync(url);

                if (response.IsSuccessStatusCode) {
                    var content = await response.Content.ReadAsStringAsync();

                    try {
                        var jsonDoc = JsonDocument.Parse(content);
                        return Ok(jsonDoc.RootElement);
                    }
                    catch (JsonException) {
                        return Content(content, "application/json");
                    }
                }

                return StatusCode((int)response.StatusCode, await response.Content.ReadAsStringAsync());
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error proxying to Discussion");
                return StatusCode(500, new { error = "Discussion service error" });
            }
        }

        [HttpGet("/{id}")]
        public async Task<IActionResult> GetReaction(string country, long tweetId, long id) {
            _logger.LogInformation($"Proxying GET /reactions/{country}/{tweetId}/{id}");

            try {
                var response = await _discussionClient.GetAsync($"/api/v1.0/reactions/{country}/{tweetId}/{id}");

                if (response.IsSuccessStatusCode) {
                    var content = await response.Content.ReadAsStringAsync();

                    try {
                        var jsonDoc = JsonDocument.Parse(content);
                        return Ok(jsonDoc.RootElement);
                    }
                    catch (JsonException) {
                        return Content(content, "application/json");
                    }
                }

                return StatusCode((int)response.StatusCode, await response.Content.ReadAsStringAsync());
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error proxying to Discussion");
                return StatusCode(500, new { error = "Discussion service error" });
            }
        }

        [HttpGet("by-tweet/{tweetId}")]
        public async Task<IActionResult> GetReactionsByTweet(long tweetId, [FromQuery] string? country = null) {
            _logger.LogInformation($"Proxying GET /reactions/by-tweet/{tweetId}");

            try {
                var url = country == null
                    ? $"/api/v1.0/reactions/by-tweet/{tweetId}"
                    : $"/api/v1.0/reactions/by-tweet/{tweetId}?country={country}";

                var response = await _discussionClient.GetAsync(url);

                if (response.IsSuccessStatusCode) {
                    var content = await response.Content.ReadAsStringAsync();

                    try {
                        var jsonDoc = JsonDocument.Parse(content);
                        return Ok(jsonDoc.RootElement);
                    }
                    catch (JsonException) {
                        return Content(content, "application/json");
                    }
                }

                return StatusCode((int)response.StatusCode, await response.Content.ReadAsStringAsync());
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error proxying to Discussion");
                return StatusCode(500, new { error = "Discussion service error" });
            }
        }

        [HttpPost]
        public async Task<IActionResult> CreateReaction([FromBody] JsonElement request) {
            _logger.LogInformation("Proxying POST /reactions to Discussion");

            try {
                var dict = JsonSerializer.Deserialize<Dictionary<string, object>>(request.GetRawText())
                    ?? new Dictionary<string, object>();

                if (!dict.ContainsKey("country")) {
                    dict["country"] = "by";
                }

                var json = JsonSerializer.Serialize(dict, _jsonOptions);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var response = await _discussionClient.PostAsync("/api/v1.0/reactions", content);
                var responseContent = await response.Content.ReadAsStringAsync();

                if (response.IsSuccessStatusCode && response.StatusCode == System.Net.HttpStatusCode.Created) {
                    var location = response.Headers.Location?.ToString();
                    if (location != null) {
                        try {
                            var jsonDoc = JsonDocument.Parse(responseContent);
                            return Created(location, jsonDoc.RootElement);
                        }
                        catch {
                            return Created(location, responseContent);
                        }
                    }
                }

                return StatusCode((int)response.StatusCode, responseContent);
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error proxying to Discussion");
                return StatusCode(500, new { error = "Discussion service error" });
            }
        }

        [HttpPut("{id:long}")]
        public async Task<IActionResult> UpdateReactionOld(long id, [FromBody] JsonElement request) {
            _logger.LogInformation($"PUT /reactions/{id} - old format, attempting to find reaction");

            try {
                var jsonContent = new StringContent(request.GetRawText(), Encoding.UTF8, "application/json");
                var response = await _discussionClient.PutAsync($"/api/v1.0/reactions/{id}", jsonContent);

                var responseContent = await response.Content.ReadAsStringAsync();

                if (response.IsSuccessStatusCode) {
                    try {
                        var jsonDoc = JsonDocument.Parse(responseContent);
                        return Ok(jsonDoc.RootElement);
                    }
                    catch {
                        return Content(responseContent, "application/json");
                    }
                }

                return StatusCode((int)response.StatusCode, responseContent);
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error proxying to Discussion");
                return StatusCode(500, new { error = "Discussion service error" });
            }
        }

        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetReactionById(long id) {
            _logger.LogInformation($"========== PROXY: GET /reactions/{id} ==========");

            try {
                var response = await _discussionClient.GetAsync($"/api/v1.0/reactions/{id}");
                var content = await response.Content.ReadAsStringAsync();

                return StatusCode((int)response.StatusCode, content);
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error getting reaction by id {id}");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeleteReactionById(long id) {
            _logger.LogInformation($"========== PROXY: DELETE /reactions/{id} ==========");

            try {
                var response = await _discussionClient.DeleteAsync($"/api/v1.0/reactions/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NoContent) {
                    return NoContent();
                }

                var content = await response.Content.ReadAsStringAsync();
                return StatusCode((int)response.StatusCode, content);
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error deleting reaction by id {id}");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }


        [HttpPut("{country}/{tweetId}/{id}")]
        public async Task<IActionResult> UpdateReaction(string country, long tweetId, long id, [FromBody] JsonElement request) {
            _logger.LogInformation($"Proxying PUT /reactions/{country}/{tweetId}/{id}");

            try {
                var jsonContent = new StringContent(request.GetRawText(), Encoding.UTF8, "application/json");
                var response = await _discussionClient.PutAsync($"/api/v1.0/reactions/{country}/{tweetId}/{id}", jsonContent);

                var responseContent = await response.Content.ReadAsStringAsync();

                if (response.IsSuccessStatusCode) {
                    try {
                        var jsonDoc = JsonDocument.Parse(responseContent);
                        return Ok(jsonDoc.RootElement);
                    }
                    catch {
                        return Content(responseContent, "application/json");
                    }
                }

                return StatusCode((int)response.StatusCode, responseContent);
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error proxying to Discussion");
                return StatusCode(500, new { error = "Discussion service error" });
            }
        }

        [HttpDelete("{country}/{tweetId}/{id}")]
        public async Task<IActionResult> DeleteReaction(string country, long tweetId, long id) {
            _logger.LogInformation($"Proxying DELETE /reactions/{country}/{tweetId}/{id}");

            try {
                var response = await _discussionClient.DeleteAsync($"/api/v1.0/reactions/{country}/{tweetId}/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NoContent) {
                    return NoContent();
                }

                var content = await response.Content.ReadAsStringAsync();
                return StatusCode((int)response.StatusCode, content);
            }
            catch (Exception ex) {
                _logger.LogError(ex, "Error proxying to Discussion");
                return StatusCode(500, new { error = "Discussion service error" });
            }
        }
    }
}