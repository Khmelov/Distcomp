using Discussion.DTO;
using Discussion.Exceptions;
using Discussion.Model;
using Discussion.Service;
using Microsoft.AspNetCore.Mvc;
using System.Text;
using System.Text.Json;

namespace Discussion.Controllers {
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionsController : ControllerBase {
        private readonly ReactionService _reactionService;
        private readonly ILogger<ReactionsController> _logger;

        public ReactionsController(ReactionService reactionService, ILogger<ReactionsController> logger) {
            _reactionService = reactionService;
            _logger = logger;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetReactions([FromQuery] string? country = null) {
            _logger.LogInformation("Discussion: Getting all reactions");
            var reactions = await _reactionService.GetAllAsync(country);
            return Ok(reactions);
        }

        [HttpGet("by-tweet/{tweetId}")]
        public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetReactionsByTweet(long tweetId, [FromQuery] string? country = null) {
            _logger.LogInformation($"Discussion: Getting reactions for tweet {tweetId}");

            var reactions = await _reactionService.GetByTweetIdAsync(tweetId, country);
            return Ok(reactions);
        }

        [HttpPost]
        public async Task<ActionResult<ReactionResponseTo>> CreateReaction([FromBody] ReactionRequestTo request) {
            _logger.LogInformation($"Discussion REST: Creating reaction for tweet {request.TweetId}");

            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try {
                request.State = request.Content.ToLower().Contains("badword")
                    ? ReactionState.DECLINE
                    : ReactionState.APPROVE;

                var reaction = await _reactionService.CreateAsync(request);

                return CreatedAtAction(
                    nameof(GetReactionById),
                    new { country = reaction.Country, tweetId = reaction.TweetId, id = reaction.Id },
                    reaction);
            }
            catch (ValidationException ex) {
                return BadRequest(new { error = ex.Message });
            }
        }

        [HttpPut("{id:long}")]
        public async Task<IActionResult> UpdateReactionOld(long id, [FromBody] ReactionRequestTo request) {
            _logger.LogInformation($"Discussion: Updating reaction {id}");

            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            request.Country = "by";
            request.Id = id;

            try {
                var updated = await _reactionService.UpdateAsync(request);
                if (updated == null)
                    return NotFound();

                return Ok(updated);
            }
            catch (ValidationException ex) {
                return BadRequest(new { error = ex.Message });
            }
        }

        [HttpPut("{country}/{tweetId}/{id}")]
        public async Task<ActionResult<ReactionResponseTo>> UpdateReaction(string country, long tweetId, long id, [FromBody] ReactionRequestTo request) {
            _logger.LogInformation($"Discussion: Updating reaction {id}");

            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            request.Country = country;
            request.TweetId = tweetId;
            request.Id = id;

            try {
                var updated = await _reactionService.UpdateAsync(request);
                if (updated == null)
                    return NotFound();

                return Ok(updated);
            }
            catch (ValidationException ex) {
                return BadRequest(new { error = ex.Message });
            }
        }

        [HttpDelete("{country}/{tweetId}/{id}")]
        public async Task<IActionResult> DeleteReaction(string country, long tweetId, long id) {
            _logger.LogInformation($"Discussion: Deleting reaction {id}");

            var deleted = await _reactionService.DeleteAsync(country, tweetId, id);
            if (!deleted)
                return NotFound();

            return NoContent();
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<ReactionResponseTo>> GetReactionById(long id) {
            _logger.LogInformation($"========== DISCUSSION: GET /find-by-id/{id} ==========");

            try {
                var reactions = await _reactionService.FindByIdAsync(id);

                if (!reactions.Any()) {
                    _logger.LogWarning($"Reaction with id {id} not found");
                    return NotFound();
                }

                return Ok(reactions.First());
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error getting reaction by id {id}");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeleteReactionById(long id) {
            _logger.LogInformation($"========== DISCUSSION: DELETE /find-by-id/{id} ==========");

            try {
                var reactions = await _reactionService.FindByIdAsync(id);

                if (!reactions.Any()) {
                    _logger.LogWarning($"Reaction with id {id} not found");
                    return NotFound();
                }

                var reaction = reactions.First();

                var deleted = await _reactionService.DeleteAsync(reaction.Country, reaction.TweetId, reaction.Id);

                if (!deleted) {
                    return NotFound();
                }

                _logger.LogInformation($"Successfully deleted reaction {id}");
                return NoContent();
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error deleting reaction by id {id}");
                return StatusCode(500, new { error = "Internal server error" });
            }
        }
    }
}