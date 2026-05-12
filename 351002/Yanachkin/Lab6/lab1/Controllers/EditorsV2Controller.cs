using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace lab1.Controllers;

[ApiController]
[Route("api/v2.0/editors")]
[Authorize]
public sealed class EditorsV2Controller : ControllerBase
{
    private readonly IEditorService _service;

    public EditorsV2Controller(IEditorService service)
    {
        _service = service;
    }

    [HttpGet]
    public async Task<IActionResult> GetEditors([FromQuery] int? page, [FromQuery] int? size, [FromQuery] string? sort = null, [FromQuery] string? login = null, CancellationToken cancellationToken = default)
    {
        var pagingRequested = page.HasValue || size.HasValue || !string.IsNullOrWhiteSpace(sort) || !string.IsNullOrWhiteSpace(login);
        if (!pagingRequested)
            return Ok(await _service.GetAllAsync(cancellationToken));

        return Ok(await _service.GetPageAsync(page ?? 0, size ?? 20, sort, login, cancellationToken));
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<EditorResponseTo>> GetById(long id, CancellationToken cancellationToken = default)
        => Ok(await _service.GetByIdAsync(id, cancellationToken));

    [HttpPost]
    [AllowAnonymous]
    public async Task<ActionResult<EditorResponseTo>> Create([FromBody] EditorRequestTo request, CancellationToken cancellationToken = default)
    {
        var created = await _service.CreateAsync(request, cancellationToken);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPut]
    [Authorize(Roles = "ADMIN")]
    public async Task<ActionResult<EditorResponseTo>> Update([FromBody] EditorRequestTo request, CancellationToken cancellationToken = default)
        => Ok(await _service.UpdateAsync(request, cancellationToken));

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    public async Task<IActionResult> Delete(long id, CancellationToken cancellationToken = default)
    {
        await _service.DeleteAsync(id, cancellationToken);
        return NoContent();
    }
}
