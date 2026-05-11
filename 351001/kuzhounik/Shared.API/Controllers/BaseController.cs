using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Shared.Application.Interfaces.Services;

namespace Shared.Controllers;

public abstract class BaseController<Id, RequestDto, ResponseDto> : ControllerBase
{
    protected readonly IService<Id, RequestDto, ResponseDto> _service;

    protected BaseController(IService<Id, RequestDto, ResponseDto> service)
    {
        _service = service;
    }

    [HttpPost]
    [ProducesResponseType(StatusCodes.Status201Created)]
    public virtual async Task<ActionResult<ResponseDto>> Create([FromBody] RequestDto request)
    {
        var result = await _service.CreateAsync(request); 
        return Created(string.Empty, result);
    }

    [HttpGet("{id}")]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public virtual async Task<ResponseDto> Get(Id id)
    {
        return await _service.GetAsync(id);
    }

    [HttpGet]
    [ProducesResponseType(StatusCodes.Status200OK)]
    public virtual async Task<IEnumerable<ResponseDto>> GetAll()
    {
        return await _service.GetAllAsync();
    }
    
    [HttpPut]
    [ProducesResponseType(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public virtual async Task<IActionResult> Update([FromBody] RequestDto request)
    {
        var result = await _service.UpdateAsync(request);
        if (result is null)
            return BadRequest();

        return Ok(result);
    }
    
    [HttpDelete("{id}")]
    [ProducesResponseType(StatusCodes.Status204NoContent)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public virtual async Task<IActionResult> Delete(Id id)
    {
        var existing = await _service.GetAsync(id);
        if (existing is null)
            return BadRequest();

        await _service.DeleteAsync(id);
        return NoContent();
    }
}