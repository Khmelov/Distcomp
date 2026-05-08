using Microsoft.AspNetCore.Mvc;
using Publisher.Dto;
using Publisher.Exceptions;
using Publisher.Service;

namespace Publisher.Controller {
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionController : BaseController<ReactionRequestTo, ReactionResponseTo> {
        private readonly ReactionService _reactionService;

        public ReactionController(ReactionService reactionService, ILogger<ReactionController> logger)
            : base(logger) {
            _reactionService = reactionService;
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<ReactionResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetReactions() {
            var reactions = await _reactionService.GetAllAsync();
            return Ok(reactions);
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(ReactionResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<ReactionResponseTo>> GetReaction(long id) {
            var reaction = await _reactionService.GetByIdAsync(id);
            return reaction == null ? NotFound() : Ok(reaction);
        }

        [HttpPost]
        [ProducesResponseType(typeof(ReactionResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<ReactionResponseTo>> CreateReaction([FromBody] ReactionRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try {
                var reaction = await _reactionService.CreateReactionAsync(request);
                return CreatedAtAction(nameof(GetReaction), new { id = reaction.Id }, reaction);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
        }

        [HttpPut]
        [ProducesResponseType(typeof(ReactionResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<ReactionResponseTo>> UpdateReaction([FromBody] ReactionRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            try {
                var updatedReaction = await _reactionService.UpdateReactionAsync(request);
                return updatedReaction == null ? NotFound() : Ok(updatedReaction);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
        }

        [HttpPut("{id:long}")]
        [ProducesResponseType(typeof(ReactionResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<ReactionResponseTo>> UpdateReaction(
            long id, [FromBody] ReactionRequestTo request) {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            request.Id = id;

            try {
                var updatedReaction = await _reactionService.UpdateReactionAsync(request);
                return updatedReaction == null ? NotFound() : Ok(updatedReaction);
            }
            catch (ValidationException ex) {
                return CreateErrorResponse(StatusCodes.Status400BadRequest, ex.Message);
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> DeleteReaction(long id) {
            var deleted = await _reactionService.DeleteAsync(id);
            return deleted ? NoContent() : NotFound();
        }
    }
}