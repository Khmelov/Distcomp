using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions;
using Publisher.src.NewsPortal.Publisher.Domain.Exceptions;

namespace Publisher.src.NewsPortal.Publisher.API.Controllers.v2;

[ApiController]
[Route("api/v2.0/creators")]
[Authorize]
public class CreatorsControllerV2 : ControllerBase
{
    private readonly ICreatorService _creatorService;
    private readonly ILogger<CreatorsControllerV2> _logger;

    public CreatorsControllerV2(ICreatorService creatorService, ILogger<CreatorsControllerV2> logger)
    {
        _creatorService = creatorService;
        _logger = logger;
    }

    private bool IsAdmin()
    {
        return User.IsInRole("ADMIN");
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<CreatorResponseTo>>> GetAllCreators()
    {
        var creators = await _creatorService.GetAllCreatorsAsync();
        return Ok(creators);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<CreatorResponseTo>> GetCreatorById(long id)
    {
        var creator = await _creatorService.GetCreatorByIdAsync(id);
        if (creator == null)
            return NotFound(new { errorMessage = "Creator not found", errorCode = "40401" });
        return Ok(creator);
    }

    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateCreator(long id, [FromBody] CreatorRequestTo creatorRequest)
    {
        if (!IsAdmin())
        {
            return StatusCode(403, new { errorMessage = "Forbidden - Admin only", errorCode = "40301" });
        }

        creatorRequest.Id = id;
        await _creatorService.UpdateCreatorAsync(creatorRequest);
        return NoContent();
    }

    [HttpDelete("{id}")]
    [Authorize(Roles = "ADMIN")]
    public async Task<IActionResult> DeleteCreator(long id)
    {
        try
        {
            await _creatorService.DeleteCreatorAsync(id);
            return NoContent();
        }
        catch (NotFoundException ex)
        {
            return NotFound(new { errorMessage = ex.Message, errorCode = "40401" });
        }
    }
}