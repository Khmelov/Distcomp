using Discussion.Models;
using Discussion.Services;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Threading.Tasks;
using Discussion.DTO;

namespace Discussion.Controllers
{
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionController : ControllerBase
    {
        private readonly IReactionService _reactionService;
        public ReactionController(IReactionService reactionService)
        {
            _reactionService = reactionService;
        }

        [HttpGet]
        public async Task<IActionResult> GetAll()
        {
            var reactions = await _reactionService.GetAllReactionsAsync();
            return Ok(reactions);
        }

        [HttpGet("{id}")]
        public async Task<IActionResult> GetById(long id)
        {
            var reaction = await _reactionService.GetReactionByIdAsync(id);
            if (reaction == null)
                return NotFound();
            return Ok(reaction);
        }

        [HttpPost]
        public async Task<IActionResult> Create([FromBody] ReactionRequestTo request)
        {
            var reaction = new Reaction
            {
                TopicId = request.TopicId,
                Content = request.Content,
            };
            await _reactionService.CreateReactionAsync(reaction);
            return CreatedAtAction(nameof(GetById), new { id = reaction.Id }, reaction);
        }

        [HttpPut]
        public async Task<IActionResult> Update([FromBody] ReactionRequestTo request)
        {
            if (!request.Id.HasValue)
                return BadRequest("ID в теле запроса обязателен.");

            long id = request.Id.Value;
            var reaction = await _reactionService.GetReactionByIdAsync(id);
            if (reaction == null)
                return NotFound();

            reaction.Content = request.Content;
            reaction.Modified = DateTime.UtcNow;

            await _reactionService.UpdateReactionAsync(id, reaction);
            return Ok(reaction);
        }

        [HttpPut("{id:long}")]
        public async Task<IActionResult> Update([FromRoute] long id, [FromBody] ReactionRequestTo request)
        {
            if (!request.Id.HasValue)
                return BadRequest("ID в теле запроса обязателен.");

            if (request.Id.Value != id)
            {
                request.Id = id;
            }

            var reaction = await _reactionService.GetReactionByIdAsync(id);
            if (reaction == null)
                return NotFound();

            reaction.Content = request.Content;
            reaction.Modified = DateTime.UtcNow;

            await _reactionService.UpdateReactionAsync(id, reaction);
            return Ok(reaction);
        }


        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(long id)
        {
            await _reactionService.DeleteReactionAsync(id);
            return NoContent();
        }
    }
}
