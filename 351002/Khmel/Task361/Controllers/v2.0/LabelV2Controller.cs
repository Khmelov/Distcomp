using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v2.0/labels")]
[Authorize]
public class LabelV2Controller : ControllerBase
{
    private readonly ILabelService _service;

    public LabelV2Controller(ILabelService service)
    {
        _service = service;
    }

    private bool IsAdmin() => User.IsInRole("ADMIN");

    [HttpGet]
    public IActionResult GetAll() => Ok(_service.GetAll());

    [HttpGet("{id}")]
    public IActionResult GetById(long id) => Ok(_service.GetById(id));

    [HttpPost]
    [Authorize(Roles = "ADMIN")]  // Only admins can create labels
    public IActionResult Create([FromBody] LabelRequestTo request)
    {
        return StatusCode(201, _service.Create(request));
    }

    [HttpPut]
    [Authorize(Roles = "ADMIN")]  // Only admins can update labels
    public IActionResult Update([FromBody] LabelRequestTo request)
    {
        return Ok(_service.Update(request));
    }

    [HttpDelete("{id}")]
    [Authorize(Roles = "ADMIN")]  // Only admins can delete labels
    public IActionResult Delete(long id)
    {
        _service.Delete(id);
        return NoContent();
    }
}