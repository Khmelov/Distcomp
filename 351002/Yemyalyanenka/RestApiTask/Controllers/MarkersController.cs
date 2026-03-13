using Microsoft.AspNetCore.Mvc;
using RestApiTask.Models.DTOs;
using RestApiTask.Services.Interfaces;
using AutoMapper;

namespace RestApiTask.Controllers;

[ApiController]
[Route("markers")]
public class MarkersController : ControllerBase
{
    private readonly IMarkerService _service;
    private readonly IMapper _mapper;

    public MarkersController(IMarkerService service, IMapper mapper)
    {
        _service = service;
        _mapper = mapper;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<MarkerResponseTo>>> GetAll() => Ok(await _service.GetAllAsync());

    [HttpGet("{id}")]
    public async Task<ActionResult<MarkerResponseTo>> GetById(long id) => Ok(await _service.GetByIdAsync(id));

    [HttpPost]
    public async Task<ActionResult<MarkerResponseTo>> Create([FromBody] MarkerRequestTo request)
    {
        var result = await _service.CreateAsync(request);
        return StatusCode(201, result);
    }

    [HttpPut]
    public async Task<ActionResult<MarkerResponseTo>> Update([FromBody] MarkerResponseTo responseDto)
    {
        var requestDto = _mapper.Map<MarkerRequestTo>(responseDto);
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