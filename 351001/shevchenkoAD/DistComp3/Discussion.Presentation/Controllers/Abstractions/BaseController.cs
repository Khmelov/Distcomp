using Discussion.Application.DTOs.Abstractions;
using Discussion.Application.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Presentation.Controllers.Abstractions;

[ApiController]
[Route("api/v1.0/[controller]")]
public abstract class BaseController<TRequest, TResponse> : ControllerBase 
    where TRequest : BaseRequestTo
    where TResponse : BaseResponseTo
{
    protected readonly IService<TRequest, TResponse> _service;

    protected BaseController(IService<TRequest, TResponse> service) {
        _service = service;
    }

    // GET: api/v1.0/entities
    [HttpGet]
    public virtual async Task<ActionResult<IEnumerable<TResponse>>> GetAll() {
        var result = await _service.GetAllAsync();
        return Ok(result);
    }

    // GET: api/v1.0/entities/5
    [HttpGet("{id:long}")]
    public virtual async Task<ActionResult<TResponse>> GetById(long id) {
        var result = await _service.GetByIdAsync(id);
        return Ok(result);
    }

    // POST: api/v1.0/entities
    [HttpPost]
    public virtual async Task<ActionResult<TResponse>> Create([FromBody] TRequest request) {
        var result = await _service.CreateAsync(request);
        return StatusCode(201, result);
    }

    // PUT: api/v1.0/entities/
    [HttpPut]
    public virtual async Task<ActionResult<TResponse>> Update([FromBody] TRequest request) {
        var result = await _service.UpdateAsync(request);
        return Ok(result);
    }

    // DELETE: api/v1.0/entities/5
    [HttpDelete("{id:long}")]
    public virtual async Task<IActionResult> Delete(long id) {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}