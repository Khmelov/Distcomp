// using Application.DTO.Request;
// using Application.Features.Notices.Commands;
// using Application.Features.Notices.Queries;
// using MediatR;
// using Microsoft.AspNetCore.Mvc;
//
// namespace API.Controllers;
//
// [ApiController]
// [Route("api/v0.9/notices")]
// public class NoticeController : ControllerBase
// {
//     private readonly ISender _sender;
//
//     public NoticeController(ISender sender)
//     {
//         _sender = sender;
//     }
//
//     [HttpGet]
//     public async Task<IActionResult> GetNotices()
//     {
//         var noticesResponseTo = await _sender.Send(new ReadNoticesQuery());
//
//         return Ok(noticesResponseTo);
//     }
//
//     [HttpGet("{id:long}", Name = "GetNoticeById")]
//     public async Task<IActionResult> GetNoticeById([FromRoute] long id)
//     {
//         var noticeResponseTo = await _sender.Send(new ReadNoticeQuery(id));
//
//         return Ok(noticeResponseTo);
//     }
//
//     [HttpPost]
//     public async Task<IActionResult> CreateNotice([FromBody] NoticeRequestTo noticeRequestTo)
//     {
//         var noticeResponseTo = await _sender.Send(new CreateNoticeCommand(noticeRequestTo));
//
//         return CreatedAtRoute(nameof(GetNoticeById), new { id = noticeResponseTo.Id }, noticeResponseTo);
//     }
//
//     [HttpPut]
//     public async Task<IActionResult> UpdateNotice([FromBody] UpdateNoticeRequestTo updateNoticeRequestTo)
//     {
//         var noticeRequestTo = new NoticeRequestTo(
//             updateNoticeRequestTo.NewsId,
//             updateNoticeRequestTo.Content);
//
//         var noticeResponseTo = await _sender.Send(new UpdateNoticeCommand(updateNoticeRequestTo.Id, noticeRequestTo));
//
//         return Ok(noticeResponseTo);
//     }
//
//     [HttpPut("{id:long}")]
//     public async Task<IActionResult> UpdateNoticeById([FromRoute] long id, [FromBody] NoticeRequestTo noticeRequestTo)
//     {
//         var noticeResponseTo = await _sender.Send(new UpdateNoticeCommand(id, noticeRequestTo));
//
//         return Ok(noticeResponseTo);
//     }
//
//     [HttpDelete("{id:long}")]
//     public async Task<IActionResult> DeleteNotice([FromRoute] long id)
//     {
//         await _sender.Send(new DeleteNoticeCommand(id));
//
//         return NoContent();
//     }
// }