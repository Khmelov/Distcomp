using AutoMapper;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RestApiTask.Models.DTOs;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Controllers;

[ApiController]
[Route("api/v2.0/writers")]
[Authorize] // общий требуемый токен для всех методов v2.0
public class WritersV2Controller : ControllerBase
{
    private readonly IWriterService _service;
    private readonly IMapper _mapper;

    public WritersV2Controller(IWriterService service, IMapper mapper)
    {
        _service = service;
        _mapper = mapper;
    }

    // GET /api/v2.0/writers - требует токен, вернёт 401 при отсутствии
    [HttpGet]
    [Authorize(Roles = "ADMIN,CUSTOMER")]
    public async Task<ActionResult<IEnumerable<WriterResponseTo>>> GetAll([FromQuery] QueryOptions? options) =>
        Ok(await _service.GetAllAsync(options));

    // GET /api/v2.0/writers/{id}
    [HttpGet("{id:long}")]
    [Authorize(Roles = "ADMIN,CUSTOMER")]
    public async Task<ActionResult<WriterResponseTo>> GetById(long id) =>
        Ok(await _service.GetByIdAsync(id));

    // PUT /api/v2.0/writers/{id}
    [HttpPut("{id:long}")]
    [Authorize(Roles = "ADMIN,CUSTOMER")]
    public async Task<ActionResult<WriterResponseTo>> Update(long id, [FromBody] WriterRequestTo request)
    {
        var result = await _service.UpdateAsync(id, request);
        return Ok(result);
    }

    // DELETE /api/v2.0/writers/{id} — только ADMIN (или реализовать владение в сервисе)
    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    public async Task<IActionResult> Delete(long id)
    {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}