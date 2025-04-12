using Application.DTO.Request;
using Application.DTO.Response;
using Application.Features.News.Queries;
using External.Contracts.Interfaces;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[ApiController]
[Route("api/v1.0/notices")]
public class ExternalNoticeController : ControllerBase
{
    private readonly INoticeExternalService<NoticeRequestTo, NoticeResponseTo> _externalService;
    private readonly ISender _sender;
    
    public ExternalNoticeController(INoticeExternalService<NoticeRequestTo, NoticeResponseTo> externalService, ISender sender)
    {
        _externalService = externalService;
        _sender = sender;
    }

    [HttpGet]
    public async Task<IActionResult> GetNotices(CancellationToken cancellationToken = default)
    {
        var noticesResponseTo = await _externalService.GetAsync(cancellationToken);

        return Ok(noticesResponseTo);
    }

    [HttpGet("{id:long}", Name = "GetNoticeById")]
    public async Task<IActionResult> GetNoticeById([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        var noticeResponseTo = await _externalService.GetByIdAsync(id, cancellationToken);

        return Ok(noticeResponseTo);
    }

    [HttpPost]
    public async Task<IActionResult> CreateNotice([FromBody] NoticeRequestTo noticeRequestTo, CancellationToken cancellationToken = default)
    {
        var newsResponseTo = await _sender.Send(new ReadNewsQuery(noticeRequestTo.NewsId), cancellationToken);
        
        var noticeResponseTo = await _externalService.CreateAsync(noticeRequestTo, cancellationToken);

        return CreatedAtRoute(nameof(GetNoticeById), new { id = noticeResponseTo.Id }, noticeResponseTo);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateNotice([FromBody] UpdateNoticeRequestTo updateNoticeRequestTo, CancellationToken cancellationToken = default)
    {
        var noticeRequestTo = new NoticeRequestTo(
            updateNoticeRequestTo.NewsId,
            updateNoticeRequestTo.Content);

        var noticeResponseTo = await _externalService.UpdateAsync(updateNoticeRequestTo.Id, noticeRequestTo, cancellationToken);

        return Ok(noticeResponseTo);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteNotice([FromRoute] long id, CancellationToken cancellationToken = default)
    {
        await _externalService.DeleteAsync(id, cancellationToken);

        return NoContent();
    }
}