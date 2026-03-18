using Microsoft.AspNetCore.Mvc;
using rest_api;
using rest_api.Dtos;
using rest_api.Services;

[ApiController]
[Route("api/v1.0/topics")]
public class TopicsController : ControllerBase
{
    private readonly IService<Topic, TopicRequestTo, TopicResponseTo> _topicService;

    public TopicsController(IService<Topic, TopicRequestTo, TopicResponseTo> topicService)
    {
        _topicService = topicService;
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<TopicResponseTo>> GetById(long id)
    {
        var topic = await _topicService.GetByIdAsync(id);
        if (topic == null)
            return NotFound(new { error = $"Topic with id {id} not found" });
        return Ok(topic);
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<TopicResponseTo>>> GetAll()
    {
        var topics = await _topicService.GetAllAsync();
        return Ok(topics);
    }

    [HttpPost]
    public async Task<ActionResult<TopicResponseTo>> Create(TopicRequestTo request)
    {
        try
        {
            var created = await _topicService.CreateAsync(request);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }
        catch (InvalidOperationException ex)
        {
            return StatusCode(403, new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(400, new { error = "Internal server error" });
        }
    }

    [HttpPut]
    public async Task<ActionResult<TopicResponseTo>> Update(TopicRequestTo request)
    {
        var id = request.Id;

        try
        {
            var updated = await _topicService.UpdateAsync(id, request);
            return Ok(updated);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { error = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return StatusCode(403, new { error = ex.Message });
        }
        catch (Exception)
        {
            return StatusCode(400, new { error = "Internal server error" });
        }
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        try
        {
            await _topicService.DeleteAsync(id);
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