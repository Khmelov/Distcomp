using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace lab1.Controllers;

[ApiController]
[Route("api/v2.0/issues")]
[Authorize]
public sealed class IssuesV2Controller : ControllerBase
{
    private readonly IIssueService _service;

    public IssuesV2Controller(IIssueService service) => _service = service;

    [HttpGet]
    public async Task<IActionResult> GetIssues([FromQuery] int? page, [FromQuery] int? size, [FromQuery] string? sort = null, [FromQuery] long? editorId = null, [FromQuery] string? title = null, CancellationToken cancellationToken = default)
    {
        var pagingRequested = page.HasValue || size.HasValue || !string.IsNullOrWhiteSpace(sort) || editorId.HasValue || !string.IsNullOrWhiteSpace(title);
        if (!pagingRequested)
            return Ok(await _service.GetAllAsync(cancellationToken));

        return Ok(await _service.GetPageAsync(page ?? 0, size ?? 20, sort, editorId, title, cancellationToken));
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<IssueResponseTo>> GetById(long id, CancellationToken cancellationToken = default)
        => Ok(await _service.GetByIdAsync(id, cancellationToken));

    [HttpPost]
    [Authorize(Roles = "ADMIN")]
    public async Task<ActionResult<IssueResponseTo>> Create([FromBody] IssueRequestTo request, CancellationToken cancellationToken = default)
    {
        var created = await _service.CreateAsync(request, cancellationToken);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPut]
    [Authorize(Roles = "ADMIN")]
    public async Task<ActionResult<IssueResponseTo>> Update([FromBody] IssueRequestTo request, CancellationToken cancellationToken = default)
        => Ok(await _service.UpdateAsync(request, cancellationToken));

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    public async Task<IActionResult> Delete(long id, CancellationToken cancellationToken = default)
    {
        await _service.DeleteAsync(id, cancellationToken);
        return NoContent();
    }
}
