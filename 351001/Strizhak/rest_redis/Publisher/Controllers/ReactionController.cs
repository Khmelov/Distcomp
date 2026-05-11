using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;
using Publisher.Repositories;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v1.0/reactions")]
    public class ReactionController : ControllerBase
    {
        private readonly KafkaReactionRepository _repository;
        private readonly ILogger<ReactionController> _logger;

        public ReactionController(KafkaReactionRepository repository, ILogger<ReactionController> logger)
        {
            _repository = repository;
            _logger = logger;
        }
        [HttpGet("{id:long}")]
        public async Task<ActionResult<ReactionResponseTo>> GetById(long id)
        {
            ReactionResponseTo reaction;
           
            {
                reaction = await _repository.FindByIdAsync(id);
            }
            if (reaction == null) return NotFound();
            return Ok(reaction);
        }
        [HttpGet]
        public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetAll()
        {
            var reactions = await _repository.GetAllAsync();
            return Ok(reactions);
        }

        [HttpPost]
        public async Task<ActionResult<ReactionResponseTo>> Create(ReactionRequestTo request)
        {
            var result = await _repository.CreateAsync(request);

            // Передаем result.Id, чтобы location был /reactions/1
            return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
        }

        [HttpPut]
        public async Task<IActionResult> Update([FromBody] ReactionRequestTo request)
        {
            try
            {
                var updated = await _repository.UpdateAsync(request);
                if (updated == null) return NotFound();

                return Ok(updated);
            }
            catch (TimeoutException)
            {
                return StatusCode(504, "Gateway Timeout: Discussion service is busy.");
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(long id)
        {
            try
            {
                await _repository.DeleteAsync(id);
                return NoContent();
            }
            catch (TimeoutException)
            {
                return StatusCode(504);
            }
        }
    }
}
