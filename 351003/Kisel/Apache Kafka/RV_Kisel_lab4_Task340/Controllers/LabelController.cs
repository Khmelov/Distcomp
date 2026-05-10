using Microsoft.AspNetCore.Mvc;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Services;

namespace RV_Kisel_lab2_Task320.Controllers;

[ApiController]
[Route("api/v1.0/labels")]
public class LabelController : ControllerBase
{
    private readonly ILabelService _service;
    public LabelController(ILabelService service) => _service = service;

    [HttpGet]
    public async Task<ActionResult> GetAll() => Ok(await _service.GetAllAsync());

    [HttpGet("{id}")]
    public async Task<ActionResult> GetById(int id)
    {
        var l = await _service.GetByIdAsync(id);
        if (l == null) return NotFound(new ErrorResponse { ErrorMessage = "Not Found", ErrorCode = "40401" });
        return Ok(l);
    }

    [HttpPost]
    public async Task<ActionResult> Create(LabelDto dto)
    {
        var result = await _service.CreateAsync(dto);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<ActionResult> Update(LabelDto dto)
    {
        await _service.UpdateAsync(dto.Id, dto);
        return Ok(dto);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        // СНАЧАЛА проверяем, существует ли метка вообще!
        var existingLabel = await _service.GetByIdAsync(id);
        if (existingLabel == null) 
        {
            // Если её нет, возвращаем 400 Bad Request, как ждут тесты (они ждут 4xx)
            return BadRequest(new ErrorResponse { ErrorMessage = "Label not found", ErrorCode = "40003" });
        }

        // Если она есть, пытаемся удалить (если она привязана к новости, сервис кинет InvalidOperationException и вернет 400 из GlobalExceptionHandler)
        await _service.DeleteAsync(id);
        return NoContent();
    }
}