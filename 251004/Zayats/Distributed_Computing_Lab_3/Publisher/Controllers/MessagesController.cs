using Microsoft.AspNetCore.Mvc;
using Publisher.DTO.RequestDTO;
using Publisher.HttpClients.Interfaces;

namespace Discussion.Controllers;

[ApiController]
[Route("api/v1.0/[controller]")]
public class MessagesController : ControllerBase
{
    private readonly IDiscussionClient _noticeClient;

    public MessagesController(IDiscussionClient noticeClient)
    {
        _noticeClient = noticeClient;
    }

    [HttpGet]
    public async Task<IActionResult> GetNotices()
    {
        var notices = await _noticeClient.GetNoticesAsync();
        return Ok(notices);
    }

    [HttpGet("{id}")]
    public async Task<IActionResult> GetNoticeById(long id)
    {
        var notice = await _noticeClient.GetNoticeByIdAsync(id);
        return Ok(notice);
    }

    [HttpPost]
    public async Task<IActionResult> CreateNotice([FromBody] MessageRequestDTO message)
    {
        var createdNotice = await _noticeClient.CreateNoticeAsync(message);
        return CreatedAtAction(nameof(CreateNotice), new { id = createdNotice.Id }, createdNotice);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateNotice([FromBody] MessageRequestDTO message)
    {
        var updatedNotice = await _noticeClient.UpdateNoticeAsync(message);
        return Ok(updatedNotice);
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteNotice(long id)
    {
        await _noticeClient.DeleteNoticeAsync(id);
        return NoContent();
    }
}