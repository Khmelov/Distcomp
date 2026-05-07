using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v1.0/labels")]
public class LabelController : ControllerBase
{
    private readonly ILabelService _service;

    public LabelController(ILabelService service)
    {
        _service = service;
    }

    [HttpGet]
    public IActionResult GetAll() => Ok(_service.GetAll());

    [HttpGet("{id}")]
    public IActionResult GetById(long id) => Ok(_service.GetById(id));

    [HttpPost]
    public IActionResult Create([FromBody] LabelRequestTo request)
        => StatusCode(201, _service.Create(request));

    [HttpPut]
    public IActionResult Update([FromBody] LabelRequestTo request)
        => Ok(_service.Update(request));

    [HttpDelete("{id}")]
    public IActionResult Delete(long id)
    {
        _service.Delete(id);
        return NoContent();
    }
}