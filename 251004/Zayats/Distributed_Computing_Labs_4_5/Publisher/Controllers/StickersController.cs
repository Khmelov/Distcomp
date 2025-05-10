using Microsoft.AspNetCore.Mvc;
using Publisher.DTO.RequestDTO;
using Publisher.Services.Interfaces;

namespace Publisher.Controllers.V1;

[ApiController]
[Route("api/v1.0/[controller]")]
public class StickersController : ControllerBase
{
    private readonly IStickerService _iStickerService;

    public StickersController(IStickerService iStickerService)
    {
        _iStickerService = iStickerService;
    }

    [HttpGet]
    public async Task<IActionResult> GetTags()
    {
        var tags = await _iStickerService.GetTagsAsync();
        return Ok(tags);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetTagById(long id)
    {
        var tag = await _iStickerService.GetTagByIdAsync(id);
        return Ok(tag);
    }

    [HttpPost]
    public async Task<IActionResult> CreateTag([FromBody] StickerRequestDTO sticker)
    {
        var createdTag = await _iStickerService.CreateTagAsync(sticker);
        return CreatedAtAction(nameof(CreateTag), new { id = createdTag.Id }, createdTag);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateTag([FromBody] StickerRequestDTO sticker)
    {
        var updatedTag = await _iStickerService.UpdateTagAsync(sticker);
        return Ok(updatedTag);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteTag(long id)
    {
        await _iStickerService.DeleteTagAsync(id);
        return NoContent();
    }
}