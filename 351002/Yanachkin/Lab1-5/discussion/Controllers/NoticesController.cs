using discussion.Models.DTO.Requests;
using discussion.Models.DTO.Responses;
using discussion.Services;
using Microsoft.AspNetCore.Mvc;

namespace discussion.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class NoticesController : ControllerBase
{
    private readonly INoticeAppService _service;

    public NoticesController(INoticeAppService service)
    {
        _service = service;
    }

    /// <summary>Без query — все записи. С page/size/sort/issueId — пагинация.</summary>
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
            var all = await _service.GetAllAsync(null, cancellationToken).ConfigureAwait(false);
            return Ok(all);
        }

        var result = await _service.GetPageAsync(page ?? 0, size ?? 20, sort, issueId, cancellationToken)
            .ConfigureAwait(false);
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
        var result = await _service.GetPageByIssueAsync(issueId, page, size, sort, cancellationToken)
            .ConfigureAwait(false);
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<NoticeResponseTo>> GetById(long id, CancellationToken cancellationToken = default)
    {
        var notice = await _service.GetByIdAsync(id, cancellationToken).ConfigureAwait(false);
        return Ok(notice);
    }

    [HttpPost]
    public async Task<ActionResult<NoticeResponseTo>> Create(
        [FromBody] NoticeRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var result = await _service.CreateAsync(request, cancellationToken).ConfigureAwait(false);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<ActionResult<NoticeResponseTo>> Update(
        [FromBody] NoticeRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var updated = await _service.UpdateAsync(request, cancellationToken).ConfigureAwait(false);
        return Ok(updated);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id, CancellationToken cancellationToken = default)
    {
        await _service.DeleteAsync(id, cancellationToken).ConfigureAwait(false);
        return NoContent();
    }

    /// <summary>Каскадное удаление обсуждений при удалении issue (внутренний контракт publisher).</summary>
    [HttpDelete("for-issue/{issueId:long}")]
    public async Task<IActionResult> DeleteForIssue(long issueId, CancellationToken cancellationToken = default)
    {
        await _service.DeleteAllForIssueAsync(issueId, cancellationToken).ConfigureAwait(false);
        return NoContent();
    }
}
