using Microsoft.AspNetCore.Mvc;
using Publisher.Dtos;
using Publisher.Entities;
using Publisher.Services;
using Publisher.Dtos;

[ApiController]
[Route("api/v1.0/tags")]
public class TagsController : ControllerBase
{
    private readonly IService<Tag, TagRequestTo, TagResponseTo> _tagService;

    public TagsController(IService<Tag, TagRequestTo, TagResponseTo> tagService)
    {
        _tagService = tagService;
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<TagResponseTo>> GetById(long id)
    {
        var tag = await _tagService.GetByIdAsync(id);
        if (tag == null)
            return NotFound(new { error = $"Tag with id {id} not found" });
        return Ok(tag);
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<TagResponseTo>>> GetAll()
    {
        var tags = await _tagService.GetAllAsync();
        return Ok(tags);
    }

    [HttpPost]
    public async Task<ActionResult<TagResponseTo>> Create(TagRequestTo request)
    {
        try
        {
            var created = await _tagService.CreateAsync(request);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }
        catch (InvalidOperationException ex)
        {
            return StatusCode(403, new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(500, new { error = "Internal server error" });
        }
    }

    [HttpPut("{id:long}")]
    public async Task<ActionResult<TagResponseTo>> Update(long id, TagRequestTo request)
    {
        //request.Id = id; // копируем id из маршрута

        try
        {
            var updated = await _tagService.UpdateAsync(id, request);
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
            await _tagService.DeleteAsync(id);
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