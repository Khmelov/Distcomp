using Discussion.DTOs;
using Discussion.Services;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Controllers.api.v1._0;
[ApiController]
[Route("api/v1.0/notices")]
public class NoticesController: ControllerBase
{
    private readonly Discussion.Services.INoticeService _noticeService;
    //private 
    public NoticesController(INoticeService noticeService)
    {
        _noticeService = noticeService;
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<NoticeResponseDTO>>> GetAllNotices()
    {
        var notices = await _noticeService.GetAllNoticesAsync();
        return Ok(notices);
    }

    [HttpGet("{id}")]
    public async Task<ActionResult<NoticeResponseDTO>> GetNoticeById(int id)
    {
        var notice = await _noticeService.GetNoticeByIdAsync(id);
        if (notice == null)
        {
            return NotFound();
        }
        return Ok(notice);
    }

    [HttpPost]
    public async Task<ActionResult<NoticeResponseDTO>> CreateNotice(NoticeRequestDTO noticeCreateDto)
    {
        var notice = await _noticeService.CreateNoticeAsync(noticeCreateDto);
        return CreatedAtAction(nameof(GetNoticeById), new { id = notice.Id }, notice);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateNotice(NoticeRequestDTO noticeUpdateDto)
    {
        try
        {
            var notice = await _noticeService.UpdateNoticeAsync(noticeUpdateDto.Id, noticeUpdateDto);
            return Ok(notice);
        }
        catch (KeyNotFoundException)
        {
            return NotFound();
        }
    }

    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteNotice(int id)
    {
        await _noticeService.DeleteNoticeAsync(id);
        return NoContent();
    }

}