using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.Services.Interfaces;

namespace Publisher.Presentation.Controllers.v2;

[ApiController]
[Route("api/v2.0/issues")]
[Authorize]
public class IssuesControllerV2 : ControllerBase
{
    private readonly IIssueService _service;

    public IssuesControllerV2(IIssueService service)
    {
        _service = service;
    }

    [HttpGet]
    [AllowAnonymous]
    public async Task<IActionResult> GetAll()
    {
        return Ok(await _service.GetAllAsync());
    }

    [HttpGet("{id:long}")]
    [AllowAnonymous]
    public async Task<IActionResult> Get(long id)
    {
        return Ok(await _service.GetByIdAsync(id));
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] IssueRequestTo req)
    {
        return StatusCode(201, await _service.CreateAsync(req));
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] IssueRequestTo req)
    {
        return Ok(await _service.UpdateAsync(req));
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}