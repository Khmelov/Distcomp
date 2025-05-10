using Microsoft.AspNetCore.Mvc;
using Publisher.DTO.RequestDTO;
using Publisher.Services.Interfaces;

namespace Discussion.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class TopicsController : ControllerBase
{
    private readonly ITopicService _topicService;

    public TopicsController(ITopicService topicService)
    {
        _topicService = topicService;
    }

    [HttpGet]
    public async Task<IActionResult> GetStories()
    {
        var stories = await _topicService.GetStoriesAsync();
        return Ok(stories);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetStoryById(long id)
    {
        var story = await _topicService.GetStoryByIdAsync(id);
        return Ok(story);
    }

    [HttpPost]
    public async Task<IActionResult> CreateStory([FromBody] TopicRequestDTO topic)
    {
        var createdStory = await _topicService.CreateStoryAsync(topic);
        return CreatedAtAction(nameof(CreateStory), new { id = createdStory.Id }, createdStory);
    }
    
    [HttpPut]
    public async Task<IActionResult> UpdateStory([FromBody] TopicRequestDTO topic)
    {
        var updatedStory = await _topicService.UpdateStoryAsync(topic);
        return Ok(updatedStory);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteStory(long id)
    {
        await _topicService.DeleteStoryAsync(id);
        return NoContent();
    }
}