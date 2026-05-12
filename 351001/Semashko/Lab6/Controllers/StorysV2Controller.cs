using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers;

[ApiController]
[Route("api/v2.0/storys")]
[Authorize]
public class StorysV2Controller : ControllerBase
{
    private readonly IBaseService<StoryRequestDto, StoryResponseDto> _storyService;
    private readonly IValidator<StoryRequestDto> _storyValidator;

    public StorysV2Controller(
        IBaseService<StoryRequestDto, StoryResponseDto> storyService,
        IValidator<StoryRequestDto> storyValidator)
    {
        _storyService = storyService;
        _storyValidator = storyValidator;
    }

    [HttpGet]
    public ActionResult<IEnumerable<StoryResponseDto>> GetStories() => Ok(_storyService.GetAll());

    [HttpGet("{id:long}")]
    public ActionResult<StoryResponseDto> GetStory(long id)
    {
        var story = _storyService.Read(id);
        if (story == null)
        {
            return NotFound(new ErrorResponseDto
            {
                ErrorMessage = "Story not found.",
                ErrorCode = "40406"
            });
        }

        return Ok(story);
    }

    [HttpPost]
    [Authorize(Roles = "ADMIN")]
    public ActionResult<StoryResponseDto> CreateStory([FromBody] StoryRequestDto dto)
    {
        var validation = _storyValidator.Validate(dto);
        if (!validation.IsValid)
        {
            return BadRequest(new ErrorResponseDto
            {
                ErrorMessage = validation.Errors.First().ErrorMessage,
                ErrorCode = "40004"
            });
        }

        var created = _storyService.Create(dto);
        return CreatedAtAction(nameof(GetStory), new { id = created?.Id }, created);
    }

    [HttpPut]
    [Authorize(Roles = "ADMIN")]
    public ActionResult<StoryResponseDto> UpdateStory([FromBody] StoryRequestDto dto) => Ok(_storyService.Update(dto));

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    public ActionResult DeleteStory(long id) => _storyService.Delete(id) ? NoContent() : NotFound(new ErrorResponseDto
    {
        ErrorMessage = "Story not found.",
        ErrorCode = "40407"
    });
}
