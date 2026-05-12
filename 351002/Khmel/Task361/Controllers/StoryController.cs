using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v1.0/stories")]
public class StoryController : ControllerBase
{
    private readonly IStoryService _service;

    public StoryController(IStoryService service)
    {
        _service = service;
    }

    [HttpGet]
    public IActionResult GetAll() => Ok(_service.GetAll());

    [HttpGet("{id}")]
    public IActionResult GetById(long id) => Ok(_service.GetById(id));

    [HttpPost]
    public IActionResult Create([FromBody] StoryRequestTo request)
        => StatusCode(201, _service.Create(request));

    [HttpPut]
    public IActionResult Update([FromBody] StoryRequestTo request)
        => Ok(_service.Update(request));

    [HttpDelete("{id}")]
    public IActionResult Delete(long id)
    {
        _service.Delete(id);
        return NoContent();
    }
}