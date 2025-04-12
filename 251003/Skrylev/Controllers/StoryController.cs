using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v1.0/stories")]
public class StorysController : ControllerBase
{
    private readonly IStoryService _storyService;

    public StorysController(IStoryService storyService, ILogger<StorysController> logger)
    {
        _storyService = storyService;
    }

    [HttpPost]
    public async Task<IActionResult> CreateStory([FromBody] StoryRequestTo storyDto)
    {
        try
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var createdStory = await _storyService.CreateStoryAsync(storyDto);
            return CreatedAtAction(nameof(GetStoryById), new { id = createdStory.Id }, createdStory);
        }
        catch (ArgumentException ex)
        {
            return BadRequest(ex.Message);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { error = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return StatusCode(StatusCodes.Status403Forbidden, new { error = "Story already exists" });
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while creating the story.");
        }
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetStoryById(int id)
    {
        try
        {
            var story = await _storyService.GetStoryByIdAsync(id);
            if (story == null)
            {
                return NotFound();
            }
            return Ok(story);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while retrieving the story.");
        }
    }

    [HttpGet]
    public async Task<IActionResult> GetAllStorys()
    {
        try
        {
            var story = await _storyService.GetAllStorysAsync();
            return Ok(story);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while retrieving story.");
        }
    }

    [HttpPut]
    public async Task<IActionResult> UpdateStory([FromBody] Story storyDto)
    {
        try
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var updatedStory = await _storyService.UpdateStoryAsync(storyDto);
            if (updatedStory == null)
            {
                return NotFound();
            }
            return Ok(updatedStory);
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while updating the story.");
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteStory(int id)
    {
        try
        {
            bool deleted = await _storyService.DeleteStoryAsync(id);
            if (!deleted)
            {
                return NotFound();
            }
            return NoContent();
        }
        catch (Exception ex)
        {
            return StatusCode(StatusCodes.Status500InternalServerError, "An error occurred while deleting the story.");
        }
    }
}
