using Microsoft.AspNetCore.Mvc;
using RV_Kisel_lab2_Task320.Models.Dtos;
using RV_Kisel_lab2_Task320.Services;

namespace RV_Kisel_lab2_Task320.Controllers;

[ApiController]
[Route("api/v1.0/posts")]
public class PostController : ControllerBase
{
    private readonly IPostService _service;
    public PostController(IPostService service) => _service = service;

    [HttpGet]
    public async Task<ActionResult> GetAll() => Ok(await _service.GetAllAsync());

    [HttpGet("{id}")]
    public async Task<ActionResult> GetById(int id)
    {
        var p = await _service.GetByIdAsync(id);
        if (p == null) return NotFound(new ErrorResponse { ErrorMessage = "Not Found", ErrorCode = "40401" });
        return Ok(p);
    }

    [HttpPost]
    public async Task<ActionResult> Create(PostDto dto)
    {
        var result = await _service.CreateAsync(dto);
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<ActionResult> Update(PostDto dto)
    {
        await _service.UpdateAsync(dto.Id, dto);
        return Ok(dto);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(int id)
    {
        var existingPost = await _service.GetByIdAsync(id);
        if (existingPost == null) 
        {
            return BadRequest(new ErrorResponse { ErrorMessage = "Post not found", ErrorCode = "40003" });
        }
        await _service.DeleteAsync(id);
        return NoContent();
    }
}