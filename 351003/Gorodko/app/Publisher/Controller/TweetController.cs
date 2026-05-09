using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Publisher.Dto;
using Publisher.Exceptions;
using Publisher.Service;

namespace Publisher.Controller {
    [ApiController]
    [Route("api/v1.0/tweets")]
    public class TweetController : BaseController<TweetRequestTo, TweetResponseTo> {
        private readonly TweetService _tweetService;

        public TweetController(TweetService tweetService, ILogger<TweetController> logger)
          : base(logger) {
            _tweetService = tweetService;
        }

        [Authorize]
        [HttpPost("/api/v2.0/tweets")]
        public async Task<IActionResult> CreateV2([FromBody] TweetRequestTo request) {
            if (!User.IsInRole("ADMIN")) {
                var currentEditorId = long.Parse(User.FindFirst("editorId")?.Value ?? "0");
                if (request.EditorId != currentEditorId)
                    return BadRequest(new { errorMessage = "You can only create tweets for yourself", errorCode = 40301 });
            }
            return Ok(await _tweetService.CreateTweetAsync(request));
        }

        [Authorize]
        [HttpDelete("/api/v2.0/tweets/{id:long}")]
        public async Task<IActionResult> DeleteV2(long id) {
            var tweet = await _tweetService.GetByIdAsync(id);
            if (tweet == null) return NotFound();

            if (!User.IsInRole("ADMIN")) {
                var currentEditorId = long.Parse(User.FindFirst("editorId")?.Value ?? "0");
                if (tweet.EditorId != currentEditorId) return Forbid();
            }

            await _tweetService.DeleteAsync(id);
            return NoContent();
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<TweetResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<TweetResponseTo>>> GetTweets() {
            var tweets = await _tweetService.GetAllAsync();
            return Ok(tweets);
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(TweetResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<TweetResponseTo>> GetTweet(long id) {
            var tweet = await _tweetService.GetByIdAsync(id);
            return tweet == null ? NotFound() : Ok(tweet);
        }

        [HttpPost]
        [ProducesResponseType(typeof(TweetResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult<TweetResponseTo>> CreateTweet([FromBody] TweetRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try {
                var tweet = await _tweetService.CreateTweetAsync(request);
                return CreatedAtAction(nameof(GetTweet), new { id = tweet.Id }, tweet);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
            catch (ForbiddenException ex) {
                return CreateErrorResponse(StatusCodes.Status403Forbidden, ex.Message);
            }
        }

        [HttpPut]
        [ProducesResponseType(typeof(TweetResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<TweetResponseTo>> UpdateTweet([FromBody] TweetRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try {
                var updatedTweet = await _tweetService.UpdateAsync(request);
                return updatedTweet == null ? NotFound() : Ok(updatedTweet);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
            catch (ForbiddenException ex) {
                return CreateErrorResponse(StatusCodes.Status403Forbidden, ex.Message);
            }
        }

        [HttpPut("{id:long}")]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<EditorResponseTo>> UpdateTweet(
        long id,
        [FromBody] TweetRequestTo tweet) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            tweet.Id = id;

            try {
                var updatedTweet = await _tweetService.UpdateAsync(tweet);
                return updatedTweet == null ? NotFound() : Ok(updatedTweet);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
            catch (ForbiddenException ex) {
                return CreateErrorResponse(StatusCodes.Status403Forbidden, ex.Message);
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> DeleteTweet(long id) {
            var deleted = await _tweetService.DeleteAsync(id);
            return deleted ? NoContent() : NotFound();
        }
    }
}