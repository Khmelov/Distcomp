using Microsoft.AspNetCore.Mvc;
using rest_api.Dtos;
using rest_api.Entities;
using rest_api.Services;

[ApiController]
[Route("api/v1.0/reactions")]
public class ReactionsController : ControllerBase
{
    private readonly IService<Reaction, ReactionRequestTo, ReactionResponseTo> _reactionService;

    public ReactionsController(IService<Reaction, ReactionRequestTo, ReactionResponseTo> reactionService)
    {
        _reactionService = reactionService;
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<ReactionResponseTo>> GetById(long id)
    {
        var reaction = await _reactionService.GetByIdAsync(id);
        if (reaction == null)
            return NotFound(new { error = $"Reaction with id {id} not found" });
        return Ok(reaction);
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<ReactionResponseTo>>> GetAll()
    {
        var reactions = await _reactionService.GetAllAsync();
        return Ok(reactions);
    }

    [HttpPost]
    public async Task<ActionResult<ReactionResponseTo>> Create(ReactionRequestTo request)
    {
        try
        {
            var created = await _reactionService.CreateAsync(request);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(500, new { error = "Internal server error" });
        }
    }

    [HttpPut("{id:long}")]
    public async Task<ActionResult<ReactionResponseTo>> Update(long id, ReactionRequestTo request)
    {
        //request.Id = id; // копируем id из маршрута

        try
        {
            var updated = await _reactionService.UpdateAsync(id, request);
            return Ok(updated);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { error = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(500, new { error = "Internal server error" });
        }
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _reactionService.DeleteAsync(id);
            return NoContent();
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(500, new { error = "Internal server error" });
        }
    }
}