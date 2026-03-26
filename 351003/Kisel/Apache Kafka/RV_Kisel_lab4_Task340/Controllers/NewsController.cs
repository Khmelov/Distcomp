using Microsoft.AspNetCore.Mvc;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Services;

namespace RV_Kisel_lab2_Task320.Controllers;

[ApiController]
[Route("api/v1.0/news")] // Прямой путь, чтобы тесты его нашли
public class NewsController : ControllerBase
{
    private readonly INewsService _service;
    public NewsController(INewsService service) => _service = service;

    [HttpGet]
    public async Task<ActionResult> GetAll() => Ok(await _service.GetAllAsync());

    [HttpGet("{id}")]
    public async Task<ActionResult> GetById(int id)
    {
        var n = await _service.GetByIdAsync(id);
        if (n == null) return NotFound(new ErrorResponse { ErrorMessage = "Not Found", ErrorCode = "40401" });
        return Ok(n);
    }

    [HttpPost]
    public async Task<ActionResult> Create(NewsDto dto)
    {
        if (await _service.ExistsByTitleAsync(dto.Title))
            return StatusCode(403, new ErrorResponse { ErrorMessage = "Duplicate title", ErrorCode = "40301" });

        var result = await _service.CreateAsync(dto);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut("{id}")]
    public async Task<ActionResult> Update(int id, [FromBody] NewsDto dto)
    {
        dto.Id = id;
        await _service.UpdateAsync(id, dto);
        return Ok(dto);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var existingCreator = await _service.GetByIdAsync(id);
        if (existingCreator == null) 
        {
            return BadRequest(new ErrorResponse { ErrorMessage = "Creator not found", ErrorCode = "40003" });
        }
        await _service.DeleteAsync(id);
        return NoContent();
    }
}