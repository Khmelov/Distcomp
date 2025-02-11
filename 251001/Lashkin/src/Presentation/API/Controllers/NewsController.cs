using Application.DTO.Request;
using Application.Features.News.Commands;
using Application.Features.News.Queries;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[ApiController]
[Route("api/v1.0/news")]
public class NewsController : ControllerBase
{
    private readonly ISender _sender;

    public NewsController(ISender sender)
    {
        _sender = sender;
    }

    [HttpGet]
    public async Task<IActionResult> GetNews()
    {
        var newsResponseTo = await _sender.Send(new ReadManyNewsQuery());

        return Ok(newsResponseTo);
    }

    [HttpGet("{id:long}", Name = "GetNewsById")]
    public async Task<IActionResult> GetNewsById([FromRoute] long id)
    {
        var newsResponseTo = await _sender.Send(new ReadNewsQuery(id));

        return Ok(newsResponseTo);
    }

    [HttpPost]
    public async Task<IActionResult> CreateNews([FromBody] NewsRequestTo newsRequestTo)
    {
        var newsResponseTo = await _sender.Send(new CreateNewsCommand(newsRequestTo));

        return CreatedAtRoute(nameof(GetNewsById), new { id = newsResponseTo.Id }, newsResponseTo);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateNews([FromBody] UpdateNewsRequestTo updateNewsRequestTo)
    {
        var newsRequestTo = new NewsRequestTo(
            updateNewsRequestTo.UserId,
            updateNewsRequestTo.Title,
            updateNewsRequestTo.Content,
            updateNewsRequestTo.Created,
            updateNewsRequestTo.Modified);

        var newsResponseTo = await _sender.Send(new UpdateNewsCommand(updateNewsRequestTo.Id, newsRequestTo));

        return Ok(newsResponseTo);
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdateNewsById([FromRoute] long id, [FromBody] NewsRequestTo newsRequestTo)
    {
        var newsResponseTo = await _sender.Send(new UpdateNewsCommand(id, newsRequestTo));

        return Ok(newsResponseTo);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteNews([FromRoute] long id)
    {
        await _sender.Send(new DeleteNewsCommand(id));

        return NoContent();
    }
}