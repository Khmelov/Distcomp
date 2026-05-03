using Microsoft.AspNetCore.Mvc;
using Shared.Dtos;
using Discussion.Services;

namespace Discussion.Controllers
{
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionController : ControllerBase
    {
        private readonly IReactionService _service;

        public ReactionController(IReactionService service)
        {
            _service = service;
        }

        // GET /api/v1.0/reactions/{topicId}/{id}
        [HttpGet("{topicId:long}/{id:long}")]
        public async Task<ActionResult<ReactionResponseTo>> GetById(long topicId, long id)
        {
            var reaction = await _service.GetByIdAsync(topicId, id);
            if (reaction == null) return NotFound();
            return Ok(reaction);
        }

        // GET /api/v1.0/reactions?topicId=...
        [HttpGet]
        public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetByTopicId([FromQuery] long topicId)
        {
            var reactions = await _service.GetByTopicIdAsync(topicId);
            return Ok(reactions);
        }

        [HttpGet("{id:long}")]  
        public async Task<ActionResult<ReactionResponseTo>> GetByIdOnly(long id)
        {
            var reaction = await _service.GetByIdOnlyAsync(id);
            if (reaction == null) return NotFound();
            return Ok(reaction);
        }

        [HttpPost]
        public async Task<ActionResult<ReactionResponseTo>> Create(ReactionRequestTo request)
        {
            var created = await _service.CreateAsync(request);
            return CreatedAtAction(nameof(GetById), new { topicId = created.TopicId, id = created.Id }, created);
        }

        [HttpPut]
        public async Task<ActionResult<ReactionResponseTo>> Update(ReactionRequestTo request)
        {
            try
            {
                var updated = await _service.UpdateAsync(request);
                return Ok(updated);
            }
            catch (KeyNotFoundException)
            {
                return NotFound();
            }
        }

        [HttpDelete("{topicId:long}/{id:long}")]
        public async Task<IActionResult> Delete(long topicId, long id)
        {
            await _service.DeleteAsync(topicId, id);
            return NoContent();
        }
    }
}