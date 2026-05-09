using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using System.Security.Claims;

[ApiController]
[Route("api/v2.0/comments")]
[Authorize]
public class CommentV2Controller : ControllerBase
{
    private readonly ICommentService _service;
    private readonly IWriterService _writerService;
    private readonly IStoryService _storyService;

    public CommentV2Controller(ICommentService service, IWriterService writerService, IStoryService storyService)
    {
        _service = service;
        _writerService = writerService;
        _storyService = storyService;
    }

    private bool IsAdmin() => User.IsInRole("ADMIN");
    private string GetCurrentUserLogin() => User.FindFirst(ClaimTypes.NameIdentifier)?.Value ?? "";

    [HttpGet]
    public IActionResult GetAll() => Ok(_service.GetAll());

    [HttpGet("{id}")]
    public IActionResult GetById(long id) => Ok(_service.GetById(id));

    [HttpPost]
    public IActionResult Create([FromBody] CommentRequestTo request)
    {
        return StatusCode(201, _service.Create(request));
    }

    [HttpPut]
    public IActionResult Update([FromBody] CommentRequestTo request)
    {
        return Ok(_service.Update(request));
    }

    [HttpDelete("{id}")]
    [Authorize(Roles = "ADMIN")]  // Only admins can delete comments
    public IActionResult Delete(long id)
    {
        _service.Delete(id);
        return NoContent();
    }
}