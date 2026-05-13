using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace lab1.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class EditorsController : ControllerBase
{
    private readonly IEditorService _service;

    public EditorsController(IEditorService service)
    {
        _service = service;
    }

    /// <summary>
    /// Без query-параметров возвращает JSON-массив редакторов (совместимость с проверяющими REST-сьютами).
    /// С параметрами page/size/sort/login — объект пагинации <see cref="PageResponseTo{EditorResponseTo}"/>.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetEditors(
        [FromQuery] int? page,
        [FromQuery] int? size,
        [FromQuery] string? sort = null,
        [FromQuery] string? login = null,
        CancellationToken cancellationToken = default)
    {
        var pagingRequested = page.HasValue || size.HasValue
            || !string.IsNullOrWhiteSpace(sort)
            || !string.IsNullOrWhiteSpace(login);

        if (!pagingRequested)
        {
            var all = await _service.GetAllAsync(cancellationToken);
            return Ok(all);
        }

        var result = await _service.GetPageAsync(page ?? 0, size ?? 20, sort, login, cancellationToken);
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<EditorResponseTo>> GetById(long id, CancellationToken cancellationToken = default)
    {
        var editor = await _service.GetByIdAsync(id, cancellationToken);
        return Ok(editor);
    }

    [HttpPost]
    public async Task<ActionResult<EditorResponseTo>> Create(
        [FromBody] EditorRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var created = await _service.CreateAsync(request, cancellationToken);
        return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
    }

    [HttpPut]
    public async Task<ActionResult<EditorResponseTo>> Update(
        [FromBody] EditorRequestTo request,
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
