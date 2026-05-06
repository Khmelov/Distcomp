using Microsoft.AspNetCore.Mvc;

[ApiController]
[Route("api/v1.0/writers")] 
public class WriterController : ControllerBase
{
    private readonly IWriterService _service;

    public WriterController(IWriterService service)
    {
        _service = service;
    }

    [HttpGet]
    public IActionResult GetAll()
    {
        var writers = _service.GetAll();
        return Ok(writers); 
    }

    [HttpGet("{id}")]
    public IActionResult GetById(long id)
    {
        var writer = _service.GetById(id);
        return Ok(writer); 
    }

    [HttpPost]
    public IActionResult Create([FromBody] WriterRequestTo request)
    {
        if (!ModelState.IsValid)
            return BadRequest(new ErrorResponse
            {
                ErrorMessage = "Некорректные данные",
                ErrorCode = 40001
            });

        var created = _service.Create(request);
        return StatusCode(201, created); 
    }

    [HttpPut]
    public IActionResult Update([FromBody] WriterRequestTo request)
    {
        if (!ModelState.IsValid)
            return BadRequest(new ErrorResponse
            {
                ErrorMessage = "Некорректные данные + не найдена запись",
                ErrorCode = 40001 
            });

        var updated = _service.Update(request);
        return Ok(updated); 
    }

    [HttpDelete("{id}")]
    public IActionResult Delete(long id)
    {
        _service.Delete(id);
        return NoContent(); 
    }
}