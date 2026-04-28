using MediatR;
using Microsoft.AspNetCore.Mvc;
using RW.Application.DTOs.Request;
using RW.Application.Features.Articles.Commands;
using RW.Application.Features.Articles.Queries;

namespace RW.Api.Controllers;

[ApiController]
[Route("api/v1.0/articles")]
public class ArticlesController : ControllerBase
{
    private readonly ISender _sender;

    public ArticlesController(ISender sender)
    {
        _sender = sender;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var result = await _sender.Send(new GetArticlesQuery());
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id)
    {
        var result = await _sender.Send(new GetArticleByIdQuery(id));
        return Ok(result);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] ArticleRequestTo dto)
    {
        var result = await _sender.Send(new CreateArticleCommand(dto));
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] ArticleRequestTo dto)
    {
        var result = await _sender.Send(new UpdateArticleCommand(dto.Id, dto));
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _sender.Send(new DeleteArticleCommand(id));
        return NoContent();
    }
}
