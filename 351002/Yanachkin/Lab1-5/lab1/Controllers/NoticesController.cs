using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace lab1.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class NoticesController : ControllerBase
{
    private readonly INoticeService _service;

    public NoticesController(INoticeService service)
    {
        _service = service;
    }

    /// <summary>
    /// Без query-параметров — массив notices. С page/size/sort/issueId — пагинация.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetNotices(
        [FromQuery] int? page,
        [FromQuery] int? size,
        [FromQuery] string? sort = null,
        [FromQuery] long? issueId = null,
        CancellationToken cancellationToken = default)
    {
        var pagingRequested = page.HasValue || size.HasValue
            || !string.IsNullOrWhiteSpace(sort)
            || issueId.HasValue;

        if (!pagingRequested)
        {
            var all = await _service.GetAllAsync(cancellationToken);
            return Ok(all);
        }

        var result = await _service.GetPageAsync(page ?? 0, size ?? 20, sort, issueId, cancellationToken);
        return Ok(result);
    }

    [HttpGet("by-issue/{issueId:long}")]
    public async Task<ActionResult<PageResponseTo<NoticeResponseTo>>> GetPageByIssue(
        long issueId,
        [FromQuery] int page = 0,
        [FromQuery] int size = 20,
        [FromQuery] string? sort = null,
        CancellationToken cancellationToken = default)
    {
        var result = await _service.GetPageByIssueAsync(issueId, page, size, sort, cancellationToken);
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<NoticeResponseTo>> GetById(long id, CancellationToken cancellationToken = default)
    {
        var notice = await _service.GetByIdAsync(id, cancellationToken);
        return Ok(notice);
    }

    [HttpPost]
    public async Task<ActionResult<NoticeResponseTo>> Create(
        [FromBody] NoticeRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var result = await _service.CreateAsync(request, cancellationToken);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<ActionResult<NoticeResponseTo>> Update(
        [FromBody] NoticeRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var updated = await _service.UpdateAsync(request, cancellationToken);
        return Ok(updated);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id, CancellationToken cancellationToken = default)
    {
        await _service.DeleteAsync(id, cancellationToken);
        return NoContent();
    }
}
