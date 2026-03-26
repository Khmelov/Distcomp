using Microsoft.AspNetCore.Mvc;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Services;

namespace RV_Kisel_lab2_Task320.Controllers;

[ApiController]
[Route("api/v1.0/creators")] // Явный путь для тестов
public class CreatorController : ControllerBase {
    private readonly ICreatorService _service;
    public CreatorController(ICreatorService service) => _service = service;

    [HttpGet] 
    public async Task<ActionResult> GetAll() => Ok(await _service.GetAllAsync());

    [HttpGet("{id}")] 
    public async Task<ActionResult> GetById(int id) {
        var r = await _service.GetByIdAsync(id);
        if (r == null) return NotFound(new ErrorResponse { ErrorMessage = "Not Found", ErrorCode = "40401" });
        return Ok(r);
    }

    [HttpPost] 
    public async Task<ActionResult> Create(CreatorDto dto) {
        var all = await _service.GetAllAsync();
        if (all.Any(x => x.Login == dto.Login)) 
            return StatusCode(403, new ErrorResponse { ErrorMessage = "Duplicate login", ErrorCode = "40301" });

        var result = await _service.CreateAsync(dto);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    // БЫЛО: [HttpPut]
    // СТАЛО:
    [HttpPut("{id}")] 
    public async Task<ActionResult> Update(int id, [FromBody] CreatorDto dto) {
        dto.Id = id; // Берем ID из URL
        await _service.UpdateAsync(id, dto);
        return Ok(dto);
    }

    [HttpDelete("{id}")] 
    public async Task<IActionResult> Delete(int id) {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}