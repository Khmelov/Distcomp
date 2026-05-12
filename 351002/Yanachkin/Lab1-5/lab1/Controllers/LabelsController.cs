using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace lab1.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class LabelsController : ControllerBase
{
    private readonly ILabelService _service;

    public LabelsController(ILabelService service)
    {
        _service = service;
    }

    /// <summary>
    /// Без query-параметров — массив labels. С page/size/sort/name — пагинация и фильтр по имени.
    /// </summary>
    [HttpGet]
    public async Task<IActionResult> GetLabels(
        [FromQuery] int? page,
        [FromQuery] int? size,
        [FromQuery] string? sort = null,
        [FromQuery] string? name = null,
        CancellationToken cancellationToken = default)
    {
        var pagingRequested = page.HasValue || size.HasValue
            || !string.IsNullOrWhiteSpace(sort)
            || !string.IsNullOrWhiteSpace(name);

        if (!pagingRequested)
        {
            var all = await _service.GetAllAsync(cancellationToken);
            return Ok(all);
        }

        var result = await _service.GetPageAsync(page ?? 0, size ?? 20, sort, name, cancellationToken);
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<ActionResult<LabelResponseTo>> GetById(long id, CancellationToken cancellationToken = default)
    {
        var label = await _service.GetByIdAsync(id, cancellationToken);
        return Ok(label);
    }

    [HttpPost]
    public async Task<ActionResult<LabelResponseTo>> Create(
        [FromBody] LabelRequestTo request,
        CancellationToken cancellationToken = default)
    {
        var result = await _service.CreateAsync(request, cancellationToken);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<ActionResult<LabelResponseTo>> Update(
        [FromBody] LabelRequestTo request,
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
