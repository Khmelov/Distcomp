using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace lab1.Controllers;

[ApiController]
[Route("api/v1.0/issues")]
public class IssuesController : ControllerBase
{
    private readonly IIssueService _service;

    public IssuesController(IIssueService service)
    {
        _service = service;
    }

    /// <summary>
    /// Без query-параметров — массив issues (как в проверяющих REST-сьютах).
    /// С page/size/sort/editorId/title — объект пагинации.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetIssues(
        [FromQuery] int? page,
        [FromQuery] int? size,
        [FromQuery] string? sort = null,
        [FromQuery] long? editorId = null,
        [FromQuery] string? title = null,
        CancellationToken cancellationToken = default)
    {
        var pagingRequested = page.HasValue || size.HasValue
            || !string.IsNullOrWhiteSpace(sort)
            || editorId.HasValue
            || !string.IsNullOrWhiteSpace(title);

        if (!pagingRequested)
        {
            var all = await _service.GetAllAsync(cancellationToken);
            return Ok(all);
        }

        var result = await _service.GetPageAsync(page ?? 0, size ?? 20, sort, editorId, title, cancellationToken);
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<IssueResponseTo>> GetById(long id, CancellationToken cancellationToken = default)
    {
        var issue = await _service.GetByIdAsync(id, cancellationToken);
        return Ok(issue);
    }

    [HttpPost]
    public async Task<ActionResult<IssueResponseTo>> Create(
        [FromBody] IssueRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var created = await _service.CreateAsync(request, cancellationToken);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPut]
    public async Task<ActionResult<IssueResponseTo>> Update(
        [FromBody] IssueRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var result = await _service.UpdateAsync(request, cancellationToken);
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id, CancellationToken cancellationToken = default)
    {
        await _service.DeleteAsync(id, cancellationToken);
        return NoContent();
    }
}
