using Microsoft.AspNetCore.Mvc;
using System.Collections.Generic;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;
using WebApplication1.DTO;
using WebApplication1.Service;

namespace WebApplication1.Controller
{
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionController : ControllerBase
    {
        private readonly IRemoteReactionService _remoteReactionService;

        public ReactionController(IRemoteReactionService remoteReactionService)
        {
            _remoteReactionService = remoteReactionService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetAll()
        {
            var result = await _remoteReactionService.GetAllReactionsAsync();
            return Ok(result);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<ReactionResponseTo>> GetById(string id)
        {
            if (!long.TryParse(id, out var reactionId))
                return BadRequest("Неверный формат идентификатора.");
            var reaction = await _remoteReactionService.GetReactionByIdAsync(reactionId);
            if (reaction == null)
                return NotFound();
            return Ok(reaction);
        }

        [HttpPost]
        public async Task<ActionResult<ReactionResponseTo>> Create([FromBody] ReactionRequestTo dto)
        {
            var created = await _remoteReactionService.CreateReactionAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        [HttpPut]
        public async Task<ActionResult<ReactionResponseTo>> Update([FromBody] ReactionRequestTo dto)
        {
            if (!dto.Id.HasValue)
                return BadRequest("ID in request body is required.");
            var updated = await _remoteReactionService.UpdateReactionAsync(dto);
            return Ok(updated);
        }

        [HttpPut("{id:long}")]
        public async Task<ActionResult<ReactionResponseTo>> Update(
            [FromRoute] long id,
            [FromBody] ReactionRequestTo dto)
        {
            if (dto?.Id != id) 
            {
                dto.Id = id;
            }

            var updated = await _remoteReactionService.UpdateReactionAsync(dto);
            return Ok(updated);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(string id)
        {
            if (!long.TryParse(id, out var reactionId))
                return BadRequest("Неверный формат идентификатора.");
            await _remoteReactionService.DeleteReactionAsync(id);
            return NoContent();
        }
    }
}
