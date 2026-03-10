using Microsoft.AspNetCore.Mvc;
using Project.Dto;
using Project.Exceptions;
using Project.Service;

namespace Project.Controller {
    [ApiController]
    [Route("api/v1.0/tweets")]
    public class TweetController : BaseController<TweetRequestTo, TweetResponseTo> {
        private readonly TweetService _tweetService;

        public TweetController(TweetService tweetService, ILogger<TweetController> logger)
            : base(logger) {
            _tweetService = tweetService;
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
        }

        [HttpPut]
        [ProducesResponseType(typeof(TweetResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<TweetResponseTo>> UpdateTweet([FromBody] TweetRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try {
                var updatedTweet = await _tweetService.UpdateTweetAsync(request);
                return updatedTweet == null ? NotFound() : Ok(updatedTweet);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
        }

        [HttpPut("{id:long}")]
        [ProducesResponseType(typeof(EditorResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<EditorResponseTo>> UpdateTweet(
        long id,
        [FromBody] TweetRequestTo tweet) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            // Устанавливаем ID из маршрута
            tweet.Id = id;

            try {
                var updatedTweet = await _tweetService.UpdateTweetAsync(tweet);
                return updatedTweet == null ? NotFound() : Ok(updatedTweet);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
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