using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v1.0/comments")]
public class CommentController : ControllerBase
{
    private readonly ICommentService _service;

    public CommentController(ICommentService service)
    {
        _service = service;
    }

    [HttpGet]
    public IActionResult GetAll() => Ok(_service.GetAll());

    [HttpGet("{id}")]
    public IActionResult GetById(long id) => Ok(_service.GetById(id));

    [HttpPost]
    public IActionResult Create([FromBody] CommentRequestTo request)
        => StatusCode(201, _service.Create(request));

    [HttpPut]
    public IActionResult Update([FromBody] CommentRequestTo request)
        => Ok(_service.Update(request));

    [HttpDelete("{id}")]
    public IActionResult Delete(long id)
    {
        _service.Delete(id);
        return NoContent();
    }
}