using Microsoft.AspNetCore.Mvc;
using Publisher.DTO.RequestDTO;
using Publisher.Services.Interfaces;

namespace Publisher.Controllers.V1;

[ApiController]
[Route("api/v1.0/[controller]")]
public class TopicsController : ControllerBase
{
    private readonly ITopicService _iTopicService;

    public TopicsController(ITopicService iTopicService)
    {
        _iTopicService = iTopicService;
    }

    [HttpGet]
    public async Task<IActionResult> GetStories()
    {
        var stories = await _iTopicService.GetStoriesAsync();
        return Ok(stories);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetStoryById(long id)
    {
        var story = await _iTopicService.GetStoryByIdAsync(id);
        return Ok(story);
    }

    [HttpPost]
    public async Task<IActionResult> CreateStory([FromBody] TopicRequestDTO topic)
    {
        var createdStory = await _iTopicService.CreateStoryAsync(topic);
        return CreatedAtAction(nameof(CreateStory), new { id = createdStory.Id }, createdStory);
    }
    
    [HttpPut]
    public async Task<IActionResult> UpdateStory([FromBody] TopicRequestDTO topic)
    {
        var updatedStory = await _iTopicService.UpdateStoryAsync(topic);
        return Ok(updatedStory);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteStory(long id)
    {
        await _iTopicService.DeleteStoryAsync(id);
        return NoContent();
    }
}