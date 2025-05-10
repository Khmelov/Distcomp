using Distributed_Computing_Lab_1.DTO.RequestDTO;
using Distributed_Computing_Lab_1.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Distributed_Computing_Lab_1.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class StickersController : ControllerBase
{
    private readonly IStickerService _stickerService;

    public StickersController(IStickerService stickerService)
    {
        _stickerService = stickerService;
    }

    [HttpGet]
    public async Task<IActionResult> GetTags()
    {
        var tags = await _stickerService.GetTagsAsync();
        return Ok(tags);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetTagById(long id)
    {
        var tag = await _stickerService.GetTagByIdAsync(id);
        return Ok(tag);
    }

    [HttpPost]
    public async Task<IActionResult> CreateTag([FromBody] StickerRequestDTO sticker)
    {
        var createdTag = await _stickerService.CreateTagAsync(sticker);
        return CreatedAtAction(nameof(CreateTag), new { id = createdTag.Id }, createdTag);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateTag([FromBody] StickerRequestDTO sticker)
    {
        var updatedTag = await _stickerService.UpdateTagAsync(sticker);
        return Ok(updatedTag);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteTag(long id)
    {
        await _stickerService.DeleteTagAsync(id);
        return NoContent();
    }
}