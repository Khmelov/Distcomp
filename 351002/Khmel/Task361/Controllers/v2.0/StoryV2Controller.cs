using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

[ApiController]
[Route("api/v2.0/stories")]
[Authorize]
public class StoryV2Controller : ControllerBase
{
    private readonly IStoryService _service;
    private readonly IWriterService _writerService;

    public StoryV2Controller(IStoryService service, IWriterService writerService)
    {
        _service = service;
        _writerService = writerService;
    }

    private bool IsAdmin() => User.IsInRole("ADMIN");
    private string GetCurrentUserLogin() => User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "";

    [HttpGet]
    public IActionResult GetAll() => Ok(_service.GetAll());

    [HttpGet("{id}")]
    public IActionResult GetById(long id) => Ok(_service.GetById(id));

    [HttpPost]
    public IActionResult Create([FromBody] StoryRequestTo request)
    {
        var writer = _writerService.GetByLogin(GetCurrentUserLogin());
        request.WriterId = writer?.Id ?? 0;
        
        return StatusCode(201, _service.Create(request));
    }

    [HttpPut]
    public IActionResult Update([FromBody] StoryRequestTo request)
    {
        var existing = _service.GetById(request.Id);
        var currentWriter = _writerService.GetByLogin(GetCurrentUserLogin());
        
        // Only admin or owner can update
        if (!IsAdmin() && existing.WriterId != currentWriter?.Id)
        {
            return Forbid();
        }

        return Ok(_service.Update(request));
    }

    [HttpDelete("{id}")]
    public IActionResult Delete(long id)
    {
        var story = _service.GetById(id);
        var currentWriter = _writerService.GetByLogin(GetCurrentUserLogin());
        
        // Only admin or owner can delete
        if (!IsAdmin() && story.WriterId != currentWriter?.Id)
        {
            return Forbid();
        }

        _service.Delete(id);
        return NoContent();
    }
}