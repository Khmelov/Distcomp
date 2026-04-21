using Microsoft.AspNetCore.Mvc;
using Publisher.Proxies;
using Publisher.Dtos;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionController : ControllerBase
    {
        private readonly IReactionProxy _proxy;

        public ReactionController(IReactionProxy proxy)
        {
            _proxy = proxy;
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<ReactionResponseTo>> GetById(long id, [FromQuery] long? topicId = null)
        {
            ReactionResponseTo reaction;
            if (topicId.HasValue)
            {
                reaction = await _proxy.GetByIdAsync(topicId.Value, id);
            }
            else
            {
                reaction = await _proxy.GetByIdOnlyAsync(id);
            }
            if (reaction == null) return NotFound();
            return Ok(reaction);
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetAll([FromQuery] long topicId)
        {
            var reactions = await _proxy.GetByTopicIdAsync(topicId);
            return Ok(reactions);
        }

        [HttpPost]
        public async Task<ActionResult<ReactionResponseTo>> Create(ReactionRequestTo request)
        {
            var created = await _proxy.CreateAsync(request);
            return CreatedAtAction(nameof(GetById), new { id = created.Id, topicId = created.TopicId }, created);
        }

        [HttpPut]
        public async Task<ActionResult<ReactionResponseTo>> Update(ReactionRequestTo request)
        {
            var updated = await _proxy.UpdateAsync(request);
            return Ok(updated);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id, [FromQuery] long topicId)
        {
            await _proxy.DeleteAsync(topicId, id);
            return NoContent();
        }
    }
}
