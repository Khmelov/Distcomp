using Discussion.DTO.Request;
using Discussion.Services.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class MessagesController : ControllerBase
{
    private readonly IMessageService _messageService;

    public MessagesController(IMessageService messageService)
    {
        _messageService = messageService;
    }
    
    [HttpGet]
    public async Task<IActionResult> GetNotices()
    {
        var notices = await _messageService.GetNoticesAsync();
        return Ok(notices);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetNoticeById(long id)
    {
        var notice = await _messageService.GetNoticeByIdAsync(id);
        return Ok(notice);
    }

    [HttpPost]
    public async Task<IActionResult> CreateNotice([FromBody] MessageRequestDTO message)
    {
        var createdNotice = await _messageService.CreateNoticeAsync(message);
        return CreatedAtAction(nameof(CreateNotice), new { id = createdNotice.Id }, createdNotice);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateNotice([FromBody] MessageRequestDTO message)
    {
        var updatedNotice = await _messageService.UpdateNoticeAsync(message);
        return Ok(updatedNotice);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteNotice(long id)
    {
        await _messageService.DeleteNoticeAsync(id);
        return NoContent();
    }
}