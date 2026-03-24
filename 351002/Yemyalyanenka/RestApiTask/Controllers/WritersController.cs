using Microsoft.AspNetCore.Mvc;
using RestApiTask.Models.DTOs;
using RestApiTask.Services.Interfaces;
using AutoMapper;

namespace RestApiTask.Controllers;

[ApiController]
[Route("writers")]
public class WritersController : ControllerBase
{
    private readonly IWriterService _service;
    private readonly IMapper _mapper;

    public WritersController(IWriterService service, IMapper mapper)
    {
        _service = service;
        _mapper = mapper;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<WriterResponseTo>>> GetAll() => Ok(await _service.GetAllAsync());

    [HttpGet("{id}")]
    public async Task<ActionResult<WriterResponseTo>> GetById(long id) => Ok(await _service.GetByIdAsync(id));

    [HttpPost]
    public async Task<ActionResult<WriterResponseTo>> Create([FromBody] WriterRequestTo request)
    {
        var result = await _service.CreateAsync(request);
        return StatusCode(201, result);
    }

    [HttpPut] // Без {id} в URL
    public async Task<ActionResult<WriterResponseTo>> Update([FromBody] WriterResponseTo responseDto)
    {
        var requestDto = _mapper.Map<WriterRequestTo>(responseDto);
        var result = await _service.UpdateAsync(responseDto.Id, requestDto);
        return Ok(result);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}